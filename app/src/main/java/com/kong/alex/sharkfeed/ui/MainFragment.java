package com.kong.alex.sharkfeed.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.kong.alex.sharkfeed.GlideApp;
import com.kong.alex.sharkfeed.GlideRequests;
import com.kong.alex.sharkfeed.utils.ImageSaver;
import com.kong.alex.sharkfeed.network.NetworkState;
import com.kong.alex.sharkfeed.api.search.Photo;
import com.kong.alex.sharkfeed.di.Injectable;
import com.kong.alex.sharkfeed.R;
import com.kong.alex.sharkfeed.utils.ZoomImageAnimator;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import timber.log.Timber;

import static com.kong.alex.sharkfeed.common.Constants.EXPANDED_IMAGE_VISIBILITY_STATE;
import static com.kong.alex.sharkfeed.common.Constants.SHARK_RV_STATE;
import static com.kong.alex.sharkfeed.common.Constants.THUMB_VIEW_STATE;

public class MainFragment extends Fragment implements Injectable, RetryCallback, SharkClickListener {

    @BindView(R.id.rv_sharks)
    RecyclerView rvSharks;
    @BindView(R.id.swipe_refresh_sharks)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.layout_expanded_image)
    SharkContentLayout sharkContentLayout;
    @BindInt(android.R.integer.config_shortAnimTime)
    int mShortAnimationDuration;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Context context;

    private SharksAdapter sharksAdapter;
    private SharkListViewModel sharkListViewModel;

    private View rootView;
    private View currentSharkView;
    private int currentSharkPosition;
    private GlideRequests glideRequest;
    private ZoomImageAnimator zoomImageAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
        glideRequest = GlideApp.with(context);
        sharkListViewModel = ViewModelProviders.of(this, viewModelFactory).get(SharkListViewModel.class);
        initAdapter();
        initSwipeRefresh();
        initZoomedImage();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            restoreViewState(savedInstanceState);
        }
    }

    private void restoreViewState(Bundle savedInstanceState) {
        int expandedImageVisibility = savedInstanceState.getInt(EXPANDED_IMAGE_VISIBILITY_STATE);
        currentSharkPosition = savedInstanceState.getInt(THUMB_VIEW_STATE);
        Parcelable rvState = savedInstanceState.getParcelable(SHARK_RV_STATE);


        sharkContentLayout.setVisibility(expandedImageVisibility);
        new Handler().postDelayed(() -> rvSharks.getLayoutManager().onRestoreInstanceState(rvState), 300);

        // Wait until the rv is populated
        rvSharks.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = rvSharks.getWidth();
                int height = rvSharks.getHeight();
                if (width > 0 && height > 0 && currentSharkView != null) {
                    rvSharks.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    zoomImageAnimator = new ZoomImageAnimator(currentSharkView, rootView, sharkContentLayout, mShortAnimationDuration);
                }
                currentSharkView = rvSharks.getLayoutManager().findViewByPosition(currentSharkPosition);
            }
        });
    }

    private void initAdapter() {
        sharksAdapter = new SharksAdapter(this, this, glideRequest);
        sharksAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(positionStart == 0) {
                    rvSharks.scrollToPosition(0);
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
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
        sharkListViewModel.getSharkPhotosResponse().observe(this, sharksAdapter::submitList);
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

    private void initZoomedImage() {
        sharkContentLayout.setCloseButtonListener(() -> zoomImageAnimator.collapseImageToThumb());
        sharkContentLayout.setFlickrButtonListener(this::openFlickrApp);
        sharkContentLayout.setDownloadButtonListener(this::downloadImage);
        sharkListViewModel.getSharkInfoResponse().observe(this, sharkContentLayout::updateSharkInfo);
        sharkListViewModel.getCurrentShark().observe(this, sharkContentLayout::updateSharkPhoto);
    }

    private void openFlickrApp(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    private void setRefreshState(NetworkState networkState) {
        Timber.d("NetworkState: %s", networkState.getStatus());
        if(networkState == NetworkState.LOADED || networkState.getStatus() == NetworkState.Status.FAILED) {
            mPtrFrame.refreshComplete();
        }
    }

    private void downloadImage(ImageSaver imageSaver) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
            return;
        }
        String savedImageLocation = imageSaver.saveImage();
        Toast.makeText(context, "Saved: " + savedImageLocation, Toast.LENGTH_LONG).show();
        addPicToGallery(savedImageLocation);
    }

    // Used to show image in gallery
    private void addPicToGallery(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imagePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void retry() {
        sharkListViewModel.retry();
    }

    @Override
    public void onClick(View view, Photo photo, int position) {
        currentSharkPosition = position;
        sharkListViewModel.setCurrentShark(photo);
        sharkListViewModel.getSharkInfoResponse(photo.getId());
        zoomImageAnimator = new ZoomImageAnimator(view, rootView, sharkContentLayout, mShortAnimationDuration);
        zoomImageAnimator.zoomImageFromThumb();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXPANDED_IMAGE_VISIBILITY_STATE, sharkContentLayout.getVisibility());
        outState.putInt(THUMB_VIEW_STATE, currentSharkPosition);
        outState.putParcelable(SHARK_RV_STATE, rvSharks.getLayoutManager().onSaveInstanceState());
    }
}
