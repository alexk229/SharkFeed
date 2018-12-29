package com.kong.alex.sharkfeed.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kong.alex.sharkfeed.NetworkState;
import com.kong.alex.sharkfeed.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_error_msg)
    TextView tvError;

    @BindView(R.id.button_retry)
    AppCompatButton buttonRetry;

    @BindView(R.id.progress_bar_network)
    ProgressBar progressBarNetwork;

    private final RetryCallback callback;

    public NetworkStateViewHolder(@NonNull View itemView, RetryCallback callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
        bindListeners();
    }

    public void bindListeners() {
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.retry();
            }
        });
    }

    public void bindTo(NetworkState networkState) {
        progressBarNetwork.setVisibility(networkState.getStatus() == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);
        buttonRetry.setVisibility(networkState.getStatus() == NetworkState.Status.FAILED ? View.VISIBLE : View.GONE);
        tvError.setVisibility(networkState.getMsg() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMsg() != null) {
            tvError.setText(networkState.getMsg());
        }
    }

    public static NetworkStateViewHolder create(ViewGroup parent, RetryCallback callback) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_network_state, parent, false);
        return new NetworkStateViewHolder(view, callback);
    }
}
