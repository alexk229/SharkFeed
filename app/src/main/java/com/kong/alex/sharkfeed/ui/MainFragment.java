package com.kong.alex.sharkfeed.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kong.alex.sharkfeed.api.Photo;
import com.kong.alex.sharkfeed.api.Photos;
import com.kong.alex.sharkfeed.di.Injectable;
import com.kong.alex.sharkfeed.R;

import java.util.ArrayList;
import java.util.List;

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
import timber.log.Timber;

public class MainFragment extends Fragment implements Injectable {

    @BindView(R.id.rv_sharks)
    RecyclerView rvSharks;

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

    private void bindViews() {
        sharksAdapter = new SharksAdapter();
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharkListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SharkListViewModel.class);
        subscribe();
    }

    private void subscribe() {
        sharkListViewModel.getPhotosResponse().observe(this, sharksAdapter::submitList);
        sharkListViewModel.getNetworkState().observe(this, sharksAdapter::setNetworkState);
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
