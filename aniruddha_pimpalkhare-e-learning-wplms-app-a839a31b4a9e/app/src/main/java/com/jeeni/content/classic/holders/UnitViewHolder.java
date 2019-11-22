package com.jeeni.content.classic.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.model.Unit;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UnitViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewUnitTitle;
    private TextView textViewUnitDescription;

    public UnitViewHolder(View itemView) {
        super(itemView);
        this.textViewUnitTitle = (TextView) itemView.findViewById(R.id.image_view_unit_title);
        this.textViewUnitDescription = (TextView)itemView.findViewById(R.id.image_view_unit_description);
    }

    public void updateUI(Unit unit, Context context){
        textViewUnitTitle.setText(unit.getTitle());
        textViewUnitDescription.setText(unit.getDescription());
    }
}
