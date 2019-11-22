package com.jeeni.content.classic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeeni.content.classic.Interfaces.PageCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.holders.PageViewHolder;
import com.jeeni.content.classic.model.Page;

import java.util.ArrayList;


/**
 * An Page adapter to display page number in PDF view activity
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class PageAdapter extends RecyclerView.Adapter<PageViewHolder>{

    private final String TAG=PageAdapter.class.getSimpleName();
    private ArrayList<Page> pages;
    private Context context;
    private PageCallBacks pageCallBacks;

    public PageAdapter(ArrayList<Page> pages, Context context, PageCallBacks pageCallBacks) {
        this.pages = pages;
        this.context = context;
        this.pageCallBacks = pageCallBacks;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_page_number,parent,false);
        return new PageViewHolder(card);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, final int position) {

        final Page page = pages.get(position);
        holder.updateUI(page,context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageCallBacks !=null)
                    pageCallBacks.onPageSelected(page,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}
