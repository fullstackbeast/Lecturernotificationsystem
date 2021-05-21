package com.ultraplex.lecturernotificationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder> {

    private ArrayList<CourseListItem> mCourseListItems;

    private  OnItemClickListener mListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class CourseListViewHolder extends  RecyclerView.ViewHolder{

        public TextView textCourseTitle;
        public TextView textCourseDepartment;
        public TextView textCourseLecturer;
        public TextView textCourseCode;
        public ImageView imgDeleteItem;
        public ImageView imgEditItem;

        public CourseListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textCourseTitle = itemView.findViewById(R.id.textview_course_title);
            textCourseDepartment = itemView.findViewById(R.id.textview_course_department);
            textCourseLecturer = itemView.findViewById(R.id.textview_course_lecturer);
            textCourseCode =itemView.findViewById(R.id.textview_course_code);
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

    public CourseListAdapter(ArrayList<CourseListItem> listItems){
        mCourseListItems = listItems;
    }

    @NonNull
    @Override
    public CourseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_item, parent, false);

        CourseListViewHolder viewHolder = new CourseListViewHolder(view, mListener);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListViewHolder holder, int position) {
        CourseListItem currentItem = mCourseListItems.get(position);
        holder.textCourseTitle.setText(currentItem.getTextCourseTitle());
        holder.textCourseDepartment.setText(currentItem.getTextCourseDepartment());
        holder.textCourseLecturer.setText(currentItem.getTextCourseLecturer());
        holder.textCourseCode.setText(currentItem.getTextCourseCode());
    }

    @Override
    public int getItemCount() {
        return mCourseListItems.size();
    }
}
