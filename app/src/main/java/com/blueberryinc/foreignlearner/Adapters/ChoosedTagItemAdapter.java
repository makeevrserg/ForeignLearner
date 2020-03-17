package com.blueberryinc.foreignlearner.Adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blueberryinc.foreignlearner.R;

import java.util.ArrayList;

public class ChoosedTagItemAdapter extends RecyclerView.Adapter<ChoosedTagItemAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mItemName;
    private OnItemClickListener mListener;
    public ChoosedTagItemAdapter(ArrayList<String> mItemOption) {
        this.mItemName = mItemOption;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectedtag_item, parent, false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoosedTagItemAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHodler:called");
        holder.textTagName.setText(mItemName.get(position));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on " + mItemName.get(position));
            }
        });
    }

    public void DeleteAt(int position){
        mItemName.remove(position);
    }
    public String getTagAt(int position){
        return mItemName.get(position);
    }
    @Override
    public int getItemCount() {
        return mItemName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTagName;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textTagName = itemView.findViewById(R.id.textViewTagName);
            textTagName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textTagName.setSingleLine(true);
            textTagName.setMarqueeRepeatLimit(-1);
            textTagName.setSelected(true);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutTags);
        }
    }
}
