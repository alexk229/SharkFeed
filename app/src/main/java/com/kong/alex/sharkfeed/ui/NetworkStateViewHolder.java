package com.kong.alex.sharkfeed.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kong.alex.NetworkState;
import com.kong.alex.sharkfeed.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_error)
    TextView tvError;

    @BindView(R.id.button_retry)
    AppCompatButton buttonRetry;

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    public NetworkStateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindTo(NetworkState networkState) {
        //error message
        tvError.setVisibility(networkState.getMsg() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMsg() != null) {
            tvError.setText(networkState.getMsg());
        }

        //loading and retry
        buttonRetry.setVisibility(networkState.getStatus() == NetworkState.Status.FAILED ? View.VISIBLE : View.GONE);
        pbLoading.setVisibility(networkState.getStatus() == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);
    }

    public static NetworkStateViewHolder create(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_network_state, parent, false);
        return new NetworkStateViewHolder(view);
    }
}
