package com.kong.alex.sharkfeed.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.alex.sharkfeed.NetworkState;
import com.kong.alex.sharkfeed.di.Injectable;
import com.kong.alex.sharkfeed.R;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import timber.log.Timber;

public class MainFragment extends Fragment implements Injectable, RetryCallback {

    @BindView(R.id.rv_sharks)
    RecyclerView rvSharks;

    @BindView(R.id.swipe_refresh_sharks)
    PtrFrameLayout mPtrFrame;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SharksAdapter sharksAdapter;
    private SharkListViewModel sharkListViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharkListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SharkListViewModel.class);
        initAdapter();
        initSwipeRefresh();
    }

    private void initAdapter() {
        sharksAdapter = new SharksAdapter(this);
        sharksAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(positionStart == 0) {
                    rvSharks.scrollToPosition(0);
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (sharksAdapter.getItemViewType(position)) {
                    case R.layout.item_network_state:
                        return 3;
                    case R.layout.item_shark:
                        return 1;
                    default:
                        return 0;
                }
            }
        });
        rvSharks.setLayoutManager(gridLayoutManager);
        rvSharks.setAdapter(sharksAdapter);

        sharkListViewModel.getPhotosResponse().observe(this, sharksAdapter::submitList);
        sharkListViewModel.getNetworkState().observe(this, sharksAdapter::setNetworkState);
    }

    private void initSwipeRefresh() {
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                sharkListViewModel.refresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        sharkListViewModel.getRefreshState().observe(this, this::setRefreshState);
    }

    private void setRefreshState(NetworkState networkState) {
        Timber.d("NetworkState: %s", networkState.getStatus());
        if(networkState == NetworkState.LOADED || networkState.getStatus() == NetworkState.Status.FAILED) {
            mPtrFrame.refreshComplete();
        }
    }

    @Override
    public void retry() {
        sharkListViewModel.retry();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
