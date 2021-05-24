package com.ultraplex.lecturernotificationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TimetableListAdapter extends RecyclerView.Adapter<TimetableListAdapter.TimetableListViewHolder> {

    private ArrayList<TimetableListItem> mListItems;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAlarmClick(int position);
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class TimetableListViewHolder extends RecyclerView.ViewHolder {

        public TextView txtStartTime;
        public TextView txtStopTime;
        public TextView txtCourseTitle;
        public TextView txtCourseCode;
        public TextView txtLevel;
        public ImageView imgAlarmItem;
        public ImageView imgDeleteItem;

        public TimetableListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            txtStartTime = itemView.findViewById(R.id.txt_timetable_starttime);
            txtStopTime = itemView.findViewById(R.id.txt_timetable_stoptime);
            txtCourseTitle = itemView.findViewById(R.id.txt_timetable_coursetitle);
            txtCourseCode = itemView.findViewById(R.id.txt_timetable_coursecode);
            txtLevel = itemView.findViewById(R.id.txt_timetable_level);
            imgAlarmItem = itemView.findViewById(R.id.img_timetable_setalarm);
            imgDeleteItem = itemView.findViewById(R.id.img_timetable_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            imgAlarmItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onAlarmClick(position);
                        }
                    }
                }
            });

            imgDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public TimetableListAdapter(ArrayList<TimetableListItem> listItems) {
        mListItems = listItems;
    }

    @NonNull
    @Override
    public TimetableListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_list_item, parent, false);

        TimetableListViewHolder viewHolder = new TimetableListViewHolder(view, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableListViewHolder holder, int position) {
        TimetableListItem currentItem = mListItems.get(position);

        holder.txtStartTime.setText(currentItem.getStartTime());
        holder.txtStopTime.setText(currentItem.getStoptime());
        holder.txtCourseTitle.setText(currentItem.getCourseTitle());
        holder.txtCourseCode.setText(currentItem.getCourseCode());
        holder.txtLevel.setText(currentItem.getLevel());
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }
}
