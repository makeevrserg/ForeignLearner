package com.blueberryinc.foreignlearner.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blueberryinc.foreignlearner.R;

import java.util.ArrayList;

public class WordEditInputElementAdapter extends RecyclerView.Adapter<WordEditInputElementAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private OnItemClickListener mListener;
    private String mHintText;
    private ArrayList<EditText> mEditTexts;
    public WordEditInputElementAdapter(String mHintText, ArrayList<EditText> mEditTexts) {
        this.mHintText = mHintText;
        this.mEditTexts=mEditTexts;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_edit_input_element_adapter, parent, false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WordEditInputElementAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHodler:called");
        mEditTexts.get(position).setHint(mHintText);
        holder.mEditTexts.setHint(mHintText);
        holder.mEditTexts.setText(mEditTexts.get(position).getText().toString());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on " + mEditTexts.get(position));
            }
        });
    }

    public void DeleteAt(int position){
        mEditTexts.remove(position);
    }
    public EditText getTagAt(int position){
        return mEditTexts.get(position);
    }
    @Override
    public int getItemCount() {
        return mEditTexts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        EditText mEditTexts;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mEditTexts = itemView.findViewById(R.id.editTextInputElementAdapter);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutWordEditAdapter);
        }
    }
}
