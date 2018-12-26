package com.kong.alex.sharkfeed.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kong.alex.NetworkState;
import com.kong.alex.sharkfeed.R;
import com.kong.alex.sharkfeed.api.Photo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SharksAdapter extends PagedListAdapter<Photo, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private NetworkState networkState;

    public SharksAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_PROGRESS) {
            return NetworkStateViewHolder.create(parent);
        } else {
            return SharkViewHolder.create(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.item_shark:
                ((SharkViewHolder) holder).bindTo(getItem(position));
                break;
            case R.layout.item_network_state:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.item_network_state;
        } else {
            return R.layout.item_shark;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        if (getCurrentList() != null) {
            if (getCurrentList().size() != 0) {
                NetworkState previousState = this.networkState;
                boolean hadExtraRow = hasExtraRow();
                this.networkState = newNetworkState;
                boolean hasExtraRow = hasExtraRow();
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount());
                    } else {
                        notifyItemInserted(super.getItemCount());
                    }
                } else if (hasExtraRow && previousState != newNetworkState) {
                    notifyItemChanged(getItemCount() - 1);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    private static DiffUtil.ItemCallback<Photo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Photo>() {
                // The ID property identifies when items are the same.
                @Override
                public boolean areItemsTheSame(Photo oldItem, Photo newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                // Use Object.equals() to know when an item's content changes.
                // Implement equals(), or write custom data comparison logic here.
                @Override
                public boolean areContentsTheSame(Photo oldItem, Photo newItem) {
                    return oldItem.equals(newItem);
                }
            };

}
