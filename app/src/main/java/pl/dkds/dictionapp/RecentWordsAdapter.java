package pl.dkds.dictionapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecentWordsAdapter extends RecyclerView.Adapter<RecentWordsAdapter.WordViewHolder> {

    private Context mCtx;
    private List<Word> wordList;

    public RecentWordsAdapter(Context mCtx, List<Word> wordList) {
        this.mCtx = mCtx;
        this.wordList = wordList;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recent_words, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Word w = wordList.get(position);
        holder.textViewTask.setText(w.getWord());
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTask;

        public WordViewHolder(View itemView) {
            super(itemView);

            textViewTask = itemView.findViewById(R.id.textViewTask);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Word word = wordList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx, WordViewActivity.class);
            intent.putExtra("GIVEN_WORD", word.getWord());

            mCtx.startActivity(intent);
        }
    }
}