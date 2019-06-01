package pl.dkds.dictionapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class GetWordDataAPI extends AsyncTask < String, Integer, String > {
    Context context;
    TextView wordTransText,
            wordDefText,
            wordExampleText;
    String givenWord;
    String translationJSON,
            definitionJSON,
            exampleSentenceJSON;
    int wordnik_RC,
            oxford_RC,
            pons_RC;


    final String x_secret = "2579db0858a7caae2a360fbc5c5b287e78139d2c3373016d3bab11687cdd593f";
//    final String app_id = "559bbd8f";
//    final String app_key = "b5d3929fe800aba81b1a2ebeceac26bc";
    final String app_id = "ecea86a3";
    final String app_key = "960682265686ea1e0d705be9797db5a5";


    GetWordDataAPI(Context context, String word, TextView translation, TextView definition, TextView example) {
        this.context = context;
        this.givenWord = word;
        this.wordTransText = translation;
        this.wordDefText = definition;
        this.wordExampleText = example;
    }

    @Override
    protected String doInBackground(String...params) {
        try {
            //download from PonsAPI
            URL url1 = new URL(urlBuilderPons(givenWord));
            HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
            con1.setRequestProperty("X-Secret", x_secret);
            pons_RC = con1.getResponseCode();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
            StringBuilder stringBuilder1 = new StringBuilder();
            String line1 = null;
            while ((line1 = reader1.readLine()) != null) {
                stringBuilder1.append(line1 + "\n");
            }
            translationJSON = stringBuilder1.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        try {
            URL url2 = new URL(urlBuilderOxford(givenWord));
            HttpsURLConnection urlConnection2 = (HttpsURLConnection) url2.openConnection();
            urlConnection2.setRequestProperty("Accept", "application/json");
            urlConnection2.setRequestProperty("app_id", app_id);
            urlConnection2.setRequestProperty("app_key", app_key);
            oxford_RC = urlConnection2.getResponseCode();

            // read the output from the server
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
            StringBuilder stringBuilder2 = new StringBuilder();

            String line2 = null;
            while ((line2 = reader2.readLine()) != null) {
                stringBuilder2.append(line2 + "\n");
            }

            definitionJSON = stringBuilder2.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        try {
            //download from WordnikAPI
            URL url3 = new URL(urlBuilderWordnik(givenWord));
            HttpsURLConnection con3 = (HttpsURLConnection) url3.openConnection();
            wordnik_RC = con3.getResponseCode();
            BufferedReader reader3 = new BufferedReader(new InputStreamReader(con3.getInputStream()));
            StringBuilder stringBuilder3 = new StringBuilder();
            String line3 = null;
            while ((line3 = reader3.readLine()) != null) {
                stringBuilder3.append(line3 + "\n");
            }
            exampleSentenceJSON = stringBuilder3.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "OK";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String translationData, definitionData, exampleSentenceData;

        if (pons_RC != 200) {
            translationData = String.valueOf(pons_RC);
            if (pons_RC == 204) {
                translationData = byIdName(context, "not_found_Pons");
            }
        } else {
            translationData = PonsUnpacker(translationJSON);
            translationData = translationData.substring(0, 1).toUpperCase() + translationData.substring(1);
        }

        if (oxford_RC != 200) {
            definitionData = String.valueOf(oxford_RC);
            if (oxford_RC == 404) {
                definitionData = byIdName(context, "not_found_Oxford");
            }
        } else {
            definitionData = OxfordUnpacker(definitionJSON);
            definitionData = definitionData.substring(0, 1).toUpperCase() + definitionData.substring(1);
        }

        if (wordnik_RC != 200) {
            exampleSentenceData = String.valueOf(wordnik_RC);
            if (wordnik_RC == 404 || wordnik_RC == 0) {
                exampleSentenceData = byIdName(context, "not_found_Wordnik");
            }
        } else {
            exampleSentenceData = WordnikUnpacker(exampleSentenceJSON);
            exampleSentenceData = exampleSentenceData.substring(0, 1).toUpperCase() + exampleSentenceData.substring(1);

        }

        wordTransText.setText(translationData);
        wordDefText.setText(definitionData);
        wordExampleText.setText(exampleSentenceData);

        Word word = new Word();
        word.setWord(givenWord);
        word.setDate(System.currentTimeMillis());
        word.setTranslation(translationData);
        word.setDefinition(definitionData);
        word.setExample(exampleSentenceData);

        if ((pons_RC == 200 || pons_RC == 204) && oxford_RC == 200 && wordnik_RC == 200) {
            new SaveWord(context, word).execute();
            Toast.makeText(context, byIdName(context, "saving_in_db"), Toast.LENGTH_SHORT).show();
        }
    }

    public static String byIdName(Context context, String name) {
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(name, "string", context.getPackageName()));
    }


    private String PonsUnpacker(String s) {
        s = s.substring(1, s.length() - 1);
        try {
            JSONObject response = new JSONObject(s);
            JSONArray reply = response.getJSONArray("hits");
            JSONObject r1 = reply.getJSONObject(0);
            JSONArray a1 = r1.getJSONArray("roms");
            JSONObject r2 = a1.getJSONObject(0);
            JSONArray a2 = r2.getJSONArray("arabs");
            JSONObject r3 = a2.getJSONObject(0);
            JSONArray a3 = r3.getJSONArray("translations");
            JSONObject r4 = a3.getJSONObject(0);
            String temp1 = r4.getString("target");
            String[] temp2 = temp1.split("<");
            if (temp2[0] == ""){
                return byIdName(context, "cannot_translate_wrong_region");
            }
            return temp2[0];
        } catch (JSONException e) {
            e.printStackTrace();
            return byIdName(context, "cannot_translate");
        }
    }

    private String OxfordUnpacker(String s) {
        try {
            JSONObject response = new JSONObject(s);
            JSONArray result = response.getJSONArray("results");
            JSONObject r1 = result.getJSONObject(0);
            JSONArray a1 = r1.getJSONArray("lexicalEntries");
            JSONObject r2 = a1.getJSONObject(0);
            JSONArray a2 = r2.getJSONArray("entries");
            JSONObject r3 = a2.getJSONObject(0);
            JSONArray a3 = r3.getJSONArray("senses");
            JSONObject r4 = a3.getJSONObject(0);
            JSONArray a4 = r4.getJSONArray("definitions");
            String def = a4.getString(0);
            return (def.substring(0, 1).toUpperCase() + def.substring(1));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String WordnikUnpacker(String s) {
        try {
            JSONObject response = new JSONObject(s);
            JSONArray reply = response.getJSONArray("examples");
            JSONObject r1 = reply.getJSONObject(0);
            String example = r1.getString("text");
            example = example.replace("_","");
            return example;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String urlBuilderWordnik(String message) {
        final String api_key = "3e7cda96325000311c00b09d82b059a61b0cd2e5d1ebfd831";
        final String word = message;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://api.wordnik.com/v4/word.json/" + word_id + "/examples?includeDuplicates=false&useCanonical=true&limit=5&api_key=" + api_key;
    }

    private String urlBuilderOxford(String message) {
        final String language = "en";
        final String word = message;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }

    private String urlBuilderPons(String message) {
        final String word = message;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://api.pons.com/v1/dictionary?l=enpl&q=" + word_id;
    }
}