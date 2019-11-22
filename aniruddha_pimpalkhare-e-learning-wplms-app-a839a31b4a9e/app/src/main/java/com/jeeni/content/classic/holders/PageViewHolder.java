package com.jeeni.content.classic.holders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.model.Page;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class PageViewHolder extends RecyclerView.ViewHolder {
    private TextView textViewPageNumber;
    private CardView cardViewPageNumber;

    public PageViewHolder(View itemView) {
        super(itemView);
        this.textViewPageNumber = (TextView) itemView.findViewById(R.id.textViewSubjectTitle);
        this.cardViewPageNumber =(CardView)itemView.findViewById(R.id.cardViewSubject);
    }

    public void updateUI(Page page, Context context) {
        textViewPageNumber.setText(String.valueOf(page.getNumber()));

        if (page.isSelected()){
            textViewPageNumber.setTextColor(Color.WHITE);
            cardViewPageNumber.setCardBackgroundColor(context.getApplicationContext().getResources().getColor(R.color.design_default_color_primary_dark));
        }
        else{
            textViewPageNumber.setTextColor(context.getApplicationContext().getResources().getColor(R.color.black_overlay));
            cardViewPageNumber.setCardBackgroundColor(Color.WHITE);
        }
    }
}
