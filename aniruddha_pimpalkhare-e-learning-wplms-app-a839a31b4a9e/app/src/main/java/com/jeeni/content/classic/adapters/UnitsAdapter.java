package com.jeeni.content.classic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.activities.UnitDetailsActivity;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.holders.UnitViewHolder;
import com.jeeni.content.classic.model.Unit;
import com.jeeni.content.classic.utils.CommonMethods;

import java.util.ArrayList;

/**
 * An Unit adapter to display unit list
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UnitsAdapter extends RecyclerView.Adapter<UnitViewHolder>{


    private final String TAG=UnitsAdapter.class.getSimpleName();
    private ArrayList<Unit> units;
    private Context context;
    private String courseName;
    private String subjectName;

    public UnitsAdapter(ArrayList<Unit> units,String courseName,String subjectName, Context context) {
        this.units = units;
        this.context = context;
        this.courseName=courseName;
        this.subjectName=subjectName;
    }

    public void setSubjectName(String subjectName){
        this.subjectName=subjectName;
    }

    @Override
    public UnitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View ticketCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_unit,parent,false);
        return new UnitViewHolder(ticketCard);
    }

    @Override
    public void onBindViewHolder(UnitViewHolder holder, int position) {

        final Unit unit = units.get(position);
        holder.updateUI(unit,context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomLog.i(TAG,"Clicked");
                Intent intent =new Intent(context, UnitDetailsActivity.class);
                intent.putExtra("courseId",unit.getCourseId());
                intent.putExtra("subjectId",unit.getSubjectId());
                intent.putExtra("unitId",unit.getId());
                intent.putExtra("courseName",courseName);
                intent.putExtra("subjectName",subjectName);
                CommonMethods.startActivity(intent, (Activity)context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return units.size();
    }
}
