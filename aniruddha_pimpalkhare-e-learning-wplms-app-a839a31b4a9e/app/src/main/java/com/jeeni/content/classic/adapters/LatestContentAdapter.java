package com.jeeni.content.classic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeeni.content.classic.Interfaces.UnitDetailsCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.holders.UnitDetailViewHolder;
import com.jeeni.content.classic.model.UnitContent;

import java.util.ArrayList;


/**
 * An Unit details adapter to display unit content list
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class LatestContentAdapter extends RecyclerView.Adapter<UnitDetailViewHolder>{

    private final String TAG= LatestContentAdapter.class.getSimpleName();
    private ArrayList<UnitContent> unitContents;
    private Context context;
    private UnitDetailsCallBacks unitDetailsCallBacks;

    public LatestContentAdapter(ArrayList<UnitContent> unitContents, Context context, UnitDetailsCallBacks unitDetailsCallBacks) {
        this.unitContents = unitContents;
        this.context = context;
        this.unitDetailsCallBacks=unitDetailsCallBacks;
    }

    @NonNull
    @Override
    public UnitDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View ticketCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_latest_unit_content,parent,false);
        return new UnitDetailViewHolder(ticketCard);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitDetailViewHolder holder, final int position) {

        final UnitContent unitContent = unitContents.get(position);
        holder.updateUI(unitContent,context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unitDetailsCallBacks!=null)
                unitDetailsCallBacks.onUnitContentSelected(unitContent,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return unitContents.size();
    }
}
