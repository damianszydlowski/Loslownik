package pl.dkds.dictionapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetRandomWord extends AsyncTask<String, Integer, String> {
    String randomWord;
    Context context;

    GetRandomWord(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url1 = new URL(urlBuilder());
            HttpsURLConnection con = (HttpsURLConnection) url1.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject response = new JSONObject(s);
            randomWord = response.getString("word");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent WordView = new Intent(context, WordViewActivity.class);
        WordView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WordView.putExtra("GIVEN_WORD", randomWord);
        context.startActivity(WordView);
    }

    private String urlBuilder(){
        final String api_key = "3e7cda96325000311c00b09d82b059a61b0cd2e5d1ebfd831";
        return "https://api.wordnik.com/v4/words.json/randomWord?hasDictionaryDef=true" +
                "&includePartOfSpeech=noun&excludePartOfSpeech=noun-plural&minCorpusCount=100" +
                "&maxCorpusCount=-1&minDictionaryCount=3&maxDictionaryCount=-1&minLength=5" +
                "&maxLength=-1&api_key=" + api_key;
    }
}