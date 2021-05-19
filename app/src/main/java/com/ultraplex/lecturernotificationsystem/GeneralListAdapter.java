package com.ultraplex.lecturernotificationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GeneralListAdapter extends RecyclerView.Adapter<GeneralListAdapter.GeneralListViewHolder> {

    private ArrayList<GeneralListItem> mListItems;

    private  OnItemClickListener mListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class GeneralListViewHolder extends  RecyclerView.ViewHolder{

        public TextView cardText;
        public ImageView imgDeleteItem;
        public ImageView imgEditItem;

        public GeneralListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            cardText = itemView.findViewById(R.id.card_text);
            imgDeleteItem = itemView.findViewById(R.id.img_delete_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            imgDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public  GeneralListAdapter(ArrayList<GeneralListItem> listItems){
        mListItems = listItems;
    }

    @NonNull
    @Override
    public GeneralListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_list_item, parent, false);

        GeneralListViewHolder viewHolder = new GeneralListViewHolder(view, mListener);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralListViewHolder holder, int position) {
        GeneralListItem currentItem = mListItems.get(position);
        holder.cardText.setText(currentItem.getmText());
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }
}
