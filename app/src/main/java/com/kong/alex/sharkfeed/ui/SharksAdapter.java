package com.kong.alex.sharkfeed.ui;

import android.view.ViewGroup;

import com.kong.alex.sharkfeed.GlideRequests;
import com.kong.alex.sharkfeed.network.NetworkState;
import com.kong.alex.sharkfeed.R;
import com.kong.alex.sharkfeed.api.search.Photo;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class SharksAdapter extends PagedListAdapter<Photo, RecyclerView.ViewHolder> {

    private NetworkState currentNetworkState;
    private final RetryCallback callback;
    private final SharkClickListener clickListener;
    private final GlideRequests glideRequests;

    private static DiffUtil.ItemCallback<Photo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Photo>() {
                // The ID property identifies when items are the same.
                @Override
                public boolean areItemsTheSame(Photo oldItem, Photo newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(Photo oldItem, Photo newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public SharksAdapter(RetryCallback callback, SharkClickListener clickListener, GlideRequests glideRequests) {
        super(DIFF_CALLBACK);
        this.callback = callback;
        this.clickListener = clickListener;
        this.glideRequests = glideRequests;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.item_shark:
                return SharkViewHolder.create(parent, clickListener, glideRequests);
            case R.layout.item_network_state:
                Timber.d("NetworkState created");
                return NetworkStateViewHolder.create(parent, callback);
            default:
                throw new IllegalArgumentException("unknown view type $viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.item_shark:
                ((SharkViewHolder) holder).bindTo(getItem(position));
                break;
            case R.layout.item_network_state:
                ((NetworkStateViewHolder) holder).bindTo(currentNetworkState);
                break;
        }
    }

    private boolean hasExtraRow() {
        return currentNetworkState != null && currentNetworkState != NetworkState.LOADED;
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
        NetworkState previousNetworkState = this.currentNetworkState;
        boolean hadExtraRow = hasExtraRow();
        this.currentNetworkState = newNetworkState;
        boolean hasExtraRow = hasExtraRow();
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasExtraRow && !previousNetworkState.equals(newNetworkState)) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

}
