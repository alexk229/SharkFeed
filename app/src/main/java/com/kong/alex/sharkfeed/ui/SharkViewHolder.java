package com.kong.alex.sharkfeed.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kong.alex.sharkfeed.R;
import com.kong.alex.sharkfeed.api.Photo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SharkViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_shark)
    ImageView ivShark;

    private Photo photo;

    SharkViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindTo(Photo photo) {
        this.photo = photo;
        Glide.with(itemView.getContext())
                .load(photo.getUrlT())
                .into(ivShark);
    }

    public static SharkViewHolder create(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_shark, parent, false);
        return new SharkViewHolder(view);
    }

    public void updatePhoto(Photo photo) {
        this.photo = photo;
    }
}
