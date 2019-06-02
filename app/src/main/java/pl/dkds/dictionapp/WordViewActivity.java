package pl.dkds.dictionapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class WordViewActivity extends AppCompatActivity {
    String givenWord;
    int exists;

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String w = null;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                w = null;
            } else {
                w = extras.getString("GIVEN_WORD");
            }
        } else {
            w = (String) savedInstanceState.getSerializable("GIVEN_WORD");
        }
        this.givenWord = w.toLowerCase();
        String ViewTitle = this.givenWord.substring(0, 1).toUpperCase() + this.givenWord.substring(1);
        getSupportActionBar().setTitle(ViewTitle);

        new CheckWord().execute();
    }

    public void fillInfoFromAPI() {
        TextView wordDefText = (TextView) findViewById(R.id.wordDef);
        TextView wordTransText = (TextView) findViewById(R.id.wordTrans);
        TextView wordExampleText = (TextView) findViewById(R.id.wordExample);
        GetWordDataAPI request = new GetWordDataAPI(this, givenWord, wordTransText, wordDefText, wordExampleText);
        request.execute();
    }

    public void fillInfoFromDB() {
        TextView wordDefText = (TextView) findViewById(R.id.wordDef);
        TextView wordTransText = (TextView) findViewById(R.id.wordTrans);
        TextView wordExampleText = (TextView) findViewById(R.id.wordExample);
        GetWordDataDB request = new GetWordDataDB(this, givenWord, wordTransText, wordDefText, wordExampleText);
        request.execute();
    }


    class CheckWord extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            exists = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .wordDao()
                    .Exists(givenWord);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exists > 0) {
                fillInfoFromDB();
            } else {
                fillInfoFromAPI();
            }
        }
    }
}
