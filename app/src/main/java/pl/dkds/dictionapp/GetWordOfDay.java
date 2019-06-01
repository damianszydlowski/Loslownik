package pl.dkds.dictionapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class GetWordOfDay extends AsyncTask<String, Integer, String> {
    String wordOfDay;
    Context context;
    TextView view;
    public String currDate;

    GetWordOfDay(Context context, TextView view){
        this.context = context;
        this.view = view;
    }

    @Override
    protected String doInBackground(String... params) {
        //download from PonsAPI
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        currDate = dateFormat.format(d);
        try {
            URL url1 = new URL(urlBuilder(currDate));
            HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
            BufferedReader reader1  = new BufferedReader(new InputStreamReader(con1.getInputStream()));
            StringBuilder stringBuilder1 = new StringBuilder();
            String line1 = null;
            while ((line1 = reader1.readLine()) != null) {
                stringBuilder1.append(line1 + "\n");
            }
            return stringBuilder1.toString();
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
            wordOfDay = response.getString("word");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.setText(wordOfDay);
    }

    private String urlBuilder(String message){
        final String api_key = "3e7cda96325000311c00b09d82b059a61b0cd2e5d1ebfd831";
        final String currDate = message;
//        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required?
        return "https://api.wordnik.com/v4/words.json/wordOfTheDay?date=/" + currDate + "&api_key=" + api_key;
    }
}
