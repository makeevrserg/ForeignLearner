package com.blueberryinc.foreignlearner.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blueberryinc.foreignlearner.R;
import com.blueberryinc.foreignlearner.WordElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordListItemAdapter extends RecyclerView.Adapter<WordListItemAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "WordListItemAdapter";


    private WordElement[] wordElement;
    public WordElement[] wordElementAll;
    OnNoteListener mOnNoteListener;

    public interface OnNoteListener {
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }

    public WordListItemAdapter(WordElement[] wordElement, OnNoteListener onNoteListener) {
        this.wordElement = wordElement;
        wordElementAll = wordElement.clone();
        mOnNoteListener = onNoteListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_wordview, parent, false);
        return new ViewHolder(view, mOnNoteListener);
    }

    private String getStringFromList(ArrayList<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : list) {
            stringBuilder.append(line).append(";");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHodler:called");

        Log.d(TAG, "onBindViewHolder: Position="+position);
        holder.mWord.setText(wordElement[position].word);
        holder.mTranscription.setText(wordElement[position].transcription);
        holder.mTranslation.setText(Arrays.toString(wordElement[position].translation).replace("[","").replace("]",""));
        holder.mTags.setText(Arrays.toString(wordElement[position].tags));
        holder.mProgressBars[0].setProgress(wordElement[position].progressBar);
        holder.mProgressBars[1].setProgress(wordElement[position].progressBar);
        holder.mProgressBars[2].setProgress(wordElement[position].progressBar);

    }

    @Override
    public int getItemCount() {
        return wordElement.length;
    }

    List<WordElement> filteredWords;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredWords = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredWords = Arrays.asList(wordElementAll);
            } else {
                for (WordElement word : wordElementAll) {
                    if (word.word.toLowerCase().contains(charSequence.toString().toLowerCase()) || word.transcription.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredWords.add(word);
                    } else {
                        boolean isFound = false;
                        for (String str : word.translation) {
                            if (str.toLowerCase().contains(charSequence)) {
                                filteredWords.add(word);
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound)
                            for (String str : word.tags) {
                                if (str.toLowerCase().contains(charSequence)) {
                                    filteredWords.add(word);
                                    break;
                                }
                            }
                    }
                }
            }
            Log.d(TAG, "performFiltering: " + Arrays.toString(filteredWords.toArray()));
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredWords;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            Arrays.fill(wordElement, null);
            wordElement = new WordElement[filteredWords.size()];

            int i = 0;
            for (WordElement word : filteredWords) {
                wordElement[i]=word;
                i++;
            }
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mWord;
        TextView mTranscription;
        TextView mTranslation;
        TextView mTags;
        ProgressBar[] mProgressBars;
        RelativeLayout relativeLayout;
        OnNoteListener onNoteListener;

        ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            mWord = itemView.findViewById(R.id.textViewWord);
            mTranscription = itemView.findViewById(R.id.textViewWordTranscription);
            mTranslation = itemView.findViewById(R.id.textViewWordTranslation);
            mTags = itemView.findViewById(R.id.textViewWordTag);
            mProgressBars = new ProgressBar[3];
            mProgressBars[0] = itemView.findViewById(R.id.progressBarSmall);
            mProgressBars[1] = itemView.findViewById(R.id.progressBarMedium);
            mProgressBars[2] = itemView.findViewById(R.id.progressBarHigh);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutWordView);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int k = getAdapterPosition();
            if (filteredWords != null)
                for (int i = 0; i < wordElementAll.length; ++i) {
                    if (wordElementAll[i].word == filteredWords.get(k).word) {
                        k = i;
                        break;
                    }
                }

            onNoteListener.onNoteClick(k);
        }

        @Override
        public boolean onLongClick(View v) {
            System.out.println("Clicked===" + getAdapterPosition());
            int k = getAdapterPosition();
            if (filteredWords != null)
                for (int i = 0; i < wordElementAll.length; ++i) {
                    if (wordElementAll[i].word == filteredWords.get(k).word) {
                        k = i;
                        break;
                    }
                }

            onNoteListener.onNoteLongClick(k);
            return true;
        }
    }

}
