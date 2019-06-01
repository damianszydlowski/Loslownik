package pl.dkds.dictionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recentWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.getting_random_word, LENGTH_SHORT).show();
                GetRandomWord request = new GetRandomWord(getApplicationContext());
                request.execute();
            }
        });

        getRecentWords();
        TextView wordOfDay = (TextView) findViewById(R.id.wordOfDay);
        GetWordOfDay request = new GetWordOfDay(this, wordOfDay);
        request.execute();
        Intent WordView = new Intent(this, WordViewActivity.class);
        WordView.putExtra("GIVEN_WORD", wordOfDay.getText().toString());
    }

    public void perform_action(View v)
    {
        Intent WordView = new Intent(this, WordViewActivity.class);
        TextView wordOfDay = (TextView) findViewById(R.id.wordOfDay);
        WordView.putExtra("GIVEN_WORD", wordOfDay.getText().toString());
        startActivity(WordView);
    }


    public void SearchInfo(View v) {
        Intent WordView = new Intent(this, WordViewActivity.class);
        EditText editText = (EditText) findViewById(R.id.word);
        String givenWord = editText.getText().toString().trim();
        String finalWord = stringValidator(givenWord);
        if ( finalWord == null) {
            Toast temp = Toast.makeText(MainActivity.this, getString(R.string.bad_word_error), Toast.LENGTH_SHORT);
            temp.show();
        } else if (finalWord == "Not a word") {
            Toast temp = Toast.makeText(MainActivity.this, getString(R.string.no_word_error), Toast.LENGTH_SHORT);
            temp.show();
        } else {
            WordView.putExtra("GIVEN_WORD", finalWord);
            startActivity(WordView);
        }
    }

    private String stringValidator (String s) {
        if (s == null || s.trim().isEmpty()) {
            return "Not a word";
        }
        Pattern p = Pattern.compile("[^A-Za-z]");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        if (b) {
            return null;
        }
        else {
            return s;
        }
    }

    private void getRecentWords() {
        class GetWords extends AsyncTask<Void, Void, List<Word>> {

            @Override
            protected List<Word> doInBackground(Void... voids) {
                List<Word> wordList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .wordDao()
                        .getrecent();
                return wordList;
            }

            @Override
            protected void onPostExecute(List<Word> tasks) {
                super.onPostExecute(tasks);
                RecentWordsAdapter adapter = new RecentWordsAdapter(MainActivity.this, tasks);
                recyclerView.setAdapter(adapter);
            }
        }

        GetWords gt = new GetWords();
        gt.execute();
    }
}
