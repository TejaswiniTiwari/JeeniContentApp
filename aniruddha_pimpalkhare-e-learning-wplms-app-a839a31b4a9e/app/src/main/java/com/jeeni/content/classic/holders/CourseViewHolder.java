package com.jeeni.content.classic.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.model.Course;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class CourseViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageViewCourseIcon;
    private TextView textViewCourseTitle;
    private TextView textViewCourseDescription;

    public CourseViewHolder(View itemView) {
        super(itemView);
        this.imageViewCourseIcon = (ImageView) itemView.findViewById(R.id.image_view_course_icon);
        this.textViewCourseTitle = (TextView) itemView.findViewById(R.id.image_view_course_title);
        this.textViewCourseDescription = (TextView)itemView.findViewById(R.id.image_view_course_description);
    }

    public void updateUI(Course course, Context context){
        Glide.with(context).load(course.getUrl()).into(imageViewCourseIcon);
        textViewCourseTitle.setText(course.getTitle());
        textViewCourseDescription.setText("Instructor: "+course.getInstructor());

    }
}
