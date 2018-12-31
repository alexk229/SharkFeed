package com.kong.alex.sharkfeed.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kong.alex.sharkfeed.GlideRequests;
import com.kong.alex.sharkfeed.R;
import com.kong.alex.sharkfeed.api.search.Photo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SharkViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_shark)
    ImageView ivShark;

    private Photo photo;

    private final SharkClickListener clickListener;
    private final GlideRequests glideRequests;

    private SharkViewHolder(@NonNull View itemView, SharkClickListener clickListener, GlideRequests glideRequests) {
        super(itemView);
        this.clickListener = clickListener;
        this.glideRequests = glideRequests;
        ButterKnife.bind(this, itemView);
        bindListeners();
    }

    private void bindListeners() {
        ivShark.setOnClickListener(v -> clickListener.onClick(v, photo, this.getAdapterPosition()));
    }

    public void bindTo(Photo photo) {
        this.photo = photo;
        glideRequests
                .load(photo.getUrlT())
                .into(ivShark);
    }

    public static SharkViewHolder create(ViewGroup parent, SharkClickListener clickListener, GlideRequests glideRequests) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_shark, parent, false);
        return new SharkViewHolder(view, clickListener, glideRequests);
    }
}
