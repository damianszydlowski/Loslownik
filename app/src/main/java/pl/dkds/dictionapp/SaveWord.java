package pl.dkds.dictionapp;

import android.content.Context;
import android.os.AsyncTask;

class SaveWord extends AsyncTask<String, Void, Void> {
    Context context;
    Word wordInstance;

    SaveWord(Context context, Word wordInstance){
        this.context = context;
        this.wordInstance = wordInstance;
    }

    @Override
    protected Void doInBackground(String... params) {
        //adding to database
        DatabaseClient.getInstance(context).getAppDatabase()
                .wordDao()
                .insert(wordInstance);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}