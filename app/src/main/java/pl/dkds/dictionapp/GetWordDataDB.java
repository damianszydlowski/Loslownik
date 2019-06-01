package pl.dkds.dictionapp;


import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class GetWordDataDB extends AsyncTask<String, Integer, String> {
    Context context;
    TextView wordTransText, wordDefText, wordExampleText;
    String givenWord;
    Word word;

    GetWordDataDB(Context context, String word, TextView translation, TextView definition, TextView example) {
        this.context = context;
        this.givenWord = word;
        this.wordTransText = translation;
        this.wordDefText = definition;
        this.wordExampleText = example;
    }

    @Override
    protected String doInBackground(String... params) {
        word = DatabaseClient.getInstance(context).getAppDatabase()
                .wordDao()
                .Get(givenWord);
        word.setDate(System.currentTimeMillis());
        DatabaseClient.getInstance(context).getAppDatabase()
                .wordDao()
                .update(word);
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        wordTransText.setText(word.getTranslation());
        wordDefText.setText(word.getDefinition());
        wordExampleText.setText(word.getExample());
        Toast.makeText(context, byIdName(context, "downloaded_from_db"), Toast.LENGTH_SHORT).show();
    }


    public static String byIdName(Context context, String name) {
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(name, "string", context.getPackageName()));
    }
}