package com.jeeni.content.classic.holders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.model.Subject;

import static com.jeeni.content.classic.activities.CourseDetailsActivity.subjects;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class SubjectViewHolder extends RecyclerView.ViewHolder {
    private TextView textViewSubjectTitle;
    private CardView cardViewSubject;

    public SubjectViewHolder(View itemView) {
        super(itemView);
        this.textViewSubjectTitle = (TextView) itemView.findViewById(R.id.textViewSubjectTitle);
        this.cardViewSubject=(CardView)itemView.findViewById(R.id.cardViewSubject);
    }

    public void updateUI(Subject subject, Context context) {
        textViewSubjectTitle.setText(subject.getTitle());

        for(Subject subject1:subjects){
            if(subject1.getId().equals(subject.getId())){
                if (subject1.isSelected()){
                    textViewSubjectTitle.setTextColor(Color.WHITE);
                    cardViewSubject.setCardBackgroundColor(context.getApplicationContext().getResources().getColor(R.color.design_default_color_primary_dark));
                }
                else{
                    textViewSubjectTitle.setTextColor(context.getApplicationContext().getResources().getColor(R.color.black_overlay));
                    cardViewSubject.setCardBackgroundColor(Color.WHITE);
                }
            }
        }
    }
}
