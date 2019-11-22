package com.jeeni.content.classic.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.model.UnitContent;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class RecentViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageViewUnitContentIcon;
    private TextView textViewUnitTitle;

    public RecentViewHolder(View itemView) {
        super(itemView);
        this.imageViewUnitContentIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
        this.textViewUnitTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
    }

    public void updateUI(UnitContent unitContent, Context context) {
        if (unitContent.isVideo()){
            imageViewUnitContentIcon.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.video));
            textViewUnitTitle.setText(unitContent.getFileName().replace(".mp4",""));
        }
        else{
            imageViewUnitContentIcon.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.pdf));
            textViewUnitTitle.setText(unitContent.getFileName().replace(".pdf",""));
        }
    }
}
