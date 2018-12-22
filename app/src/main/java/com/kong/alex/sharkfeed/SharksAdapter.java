package com.kong.alex.sharkfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SharksAdapter extends RecyclerView.Adapter<SharksAdapter.SharksHolder> {

    List<Photo> sharkList;
    Context context;

    public SharksAdapter(Context context, List<Photo> sharkList) {
        this.sharkList = sharkList;
        this.context = context;
    }


    @NonNull
    @Override
    public SharksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shark, parent, false);
        return new SharksHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharksHolder holder, int position) {
        Photo shark = sharkList.get(position);
        Glide.with(context)
                .load(shark.getUrlT())
                .into(holder.ivShark);
    }

    @Override
    public int getItemCount() {
        return sharkList.size();
    }

    class SharksHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_shark)
        ImageView ivShark;

        SharksHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
