package com.jello.zero;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kimpham on 4/29/17.
 */

public class RecyclerApdapter extends RecyclerView.Adapter<RecyclerApdapter.MyViewHolder> {

    private List<Alert> alertList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView alertTextView;

        public MyViewHolder(View view) {
            super(view);
            alertTextView = (TextView) view.findViewById(R.id.item_alert);
        }
    }


    public RecyclerApdapter(List<Alert> alertList){
        this.alertList = alertList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Alert alert = alertList.get(position);
        holder.alertTextView.setText(alert.toString());

    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }


}
