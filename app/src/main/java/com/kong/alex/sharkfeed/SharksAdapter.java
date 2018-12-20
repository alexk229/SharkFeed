package com.kong.alex.sharkfeed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SharksAdapter extends RecyclerView.Adapter<SharksAdapter.SharksHolder> {

    List<Shark> sharkList;
    Context context;

    public SharksAdapter(List<Shark> sharkList, Context context) {
        this.sharkList = sharkList;
        this.context = context;
    }


    @NonNull
    @Override
    public SharksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SharksHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SharksHolder extends RecyclerView.ViewHolder {

        public SharksHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
