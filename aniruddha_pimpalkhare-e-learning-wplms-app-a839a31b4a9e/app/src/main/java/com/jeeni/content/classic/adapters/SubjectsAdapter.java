package com.jeeni.content.classic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeeni.content.classic.Interfaces.CourseDetailsCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.holders.SubjectViewHolder;
import com.jeeni.content.classic.model.Subject;

import java.util.ArrayList;


/**
 * An Subject adapter to display subject list
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class SubjectsAdapter extends RecyclerView.Adapter<SubjectViewHolder>{

    private final String TAG=SubjectsAdapter.class.getSimpleName();
    private ArrayList<Subject> subjects;
    private Context context;
    private CourseDetailsCallBacks courseDetailsCallBacks;

    public SubjectsAdapter(ArrayList<Subject> subjects, Context context,CourseDetailsCallBacks courseDetailsCallBacks) {
        this.subjects = subjects;
        this.context = context;
        this.courseDetailsCallBacks=courseDetailsCallBacks;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subject,parent,false);
        return new SubjectViewHolder(card);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, final int position) {

        final Subject subject = subjects.get(position);
        holder.updateUI(subject,context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(courseDetailsCallBacks!=null)
                    courseDetailsCallBacks.onSubjectSelected(subject,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }
}
