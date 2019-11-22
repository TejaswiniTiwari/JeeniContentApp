package com.jeeni.content.classic.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.model.UnitContent;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UnitDetailViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageViewUnitContentIcon;
    private TextView textViewUnitTitle;
    private TextView textViewUnitSubTitle;
    //To show progress bar
    private TextView textViewProgress;
    private ProgressBar progressBar;

    public UnitDetailViewHolder(View itemView) {
        super(itemView);
        this.imageViewUnitContentIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
        this.textViewUnitTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        this.textViewUnitSubTitle = (TextView) itemView.findViewById(R.id.textViewSubTitle);
        this.textViewProgress=(TextView)itemView.findViewById(R.id.textViewProgress);
        this.progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);
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

        if(unitContent.isDownloaded()) {
            textViewProgress.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            textViewUnitSubTitle.setText(context.getResources().getString(R.string.downloaded));
            textViewUnitSubTitle.setTextColor(context.getApplicationContext().getResources().getColor(R.color.green));
        }
        else if(unitContent.isDownloading()) {
            textViewProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            textViewProgress.setText(unitContent.getProgress()+" %");
            progressBar.setProgress((int)unitContent.getProgress());
            textViewUnitSubTitle.setText(context.getResources().getString(R.string.downloading));
            textViewUnitSubTitle.setTextColor(context.getApplicationContext().getResources().getColor(R.color.yellow));
        }
        else {
            textViewProgress.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            textViewUnitSubTitle.setText(context.getResources().getString(R.string.download));
            textViewUnitSubTitle.setTextColor(context.getApplicationContext().getResources().getColor(R.color.red));
        }
    }
}
