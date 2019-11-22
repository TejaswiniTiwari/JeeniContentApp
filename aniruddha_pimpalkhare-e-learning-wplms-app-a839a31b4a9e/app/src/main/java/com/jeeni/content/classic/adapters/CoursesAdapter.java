package com.jeeni.content.classic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.activities.CourseDetailsActivity;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.holders.CourseViewHolder;
import com.jeeni.content.classic.model.Course;
import com.jeeni.content.classic.services.ForeGroundAutoSyncService;
import com.jeeni.content.classic.utils.CommonMethods;

import java.util.ArrayList;


/**
 * An Course adapter to display course list
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class CoursesAdapter extends RecyclerView.Adapter<CourseViewHolder>{

    private final static String TAG=CoursesAdapter.class.getSimpleName();
    private ArrayList<Course> courses;
    private Context context;

    public CoursesAdapter(ArrayList<Course> courses,Context context) {
        this.courses = courses;
        this.context = context;

    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View ticketCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_course,parent,false);
        return new CourseViewHolder(ticketCard);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, final int position) {

        final Course course = courses.get(position);
        holder.updateUI(course,context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
                CustomLog.i(TAG,"Clicked");
                Intent intent =new Intent(context, CourseDetailsActivity.class);
                intent.putExtra("courseId",courses.get(position).getId());
                intent.putExtra("courseName",courses.get(position).getTitle());
                CommonMethods.startActivity(intent, (Activity)context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }


    /**
     * The Foreground Sync service to get latest data from server
     */
    private void sync() {
        Intent intent = new Intent(context.getApplicationContext(), ForeGroundAutoSyncService.class);
        if (!ForeGroundAutoSyncService.isServiceRunning) {
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else
                context.startService(intent);
        } else {
            Toast.makeText(context, "Sync in progress", Toast.LENGTH_SHORT).show();
        }
    }
}
