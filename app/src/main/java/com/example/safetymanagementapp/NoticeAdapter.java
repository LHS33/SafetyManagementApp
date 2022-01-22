package com.example.safetymanagementapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    ArrayList<Notice> items = new ArrayList<Notice>();

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.notice_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, int position) {
        Notice item = items.get(position);
        holder.setItem(item);
        holder.setLayout();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layoutNotice;
        TextView notice_title;
        TextView notice_date;
        TextView notice_detail;
        ImageView notice_check;

        public ViewHolder(View itemView){
            super(itemView);
            layoutNotice = itemView.findViewById(R.id.layoutNotice);
            notice_title = itemView.findViewById(R.id.notice_title);
            notice_date = itemView.findViewById(R.id.notice_date);
            notice_detail = itemView.findViewById(R.id.notice_detail);
            notice_check = itemView.findViewById(R.id.notice_check);
        }
        public void setItem (Notice item){ notice_title.setText(item.getTitle()); }

        public void setLayout(){layoutNotice.setVisibility(View.VISIBLE);}
    }
    public void setItems(ArrayList<Notice> items){
        this.items = items;
    }

}
