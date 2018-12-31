package com.kong.alex.sharkfeed.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.kong.alex.sharkfeed.GlideApp;
import com.kong.alex.sharkfeed.GlideRequests;
import com.kong.alex.sharkfeed.R;
import com.kong.alex.sharkfeed.api.info.Owner;
import com.kong.alex.sharkfeed.api.info.PhotoInfo;
import com.kong.alex.sharkfeed.api.info.PhotoInfoResult;
import com.kong.alex.sharkfeed.api.search.Photo;
import com.kong.alex.sharkfeed.utils.ImageSaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.kong.alex.sharkfeed.common.Constants.SHARK_CONTENT_PARCELABLE;

public class SharkContentLayout extends LinearLayout implements View.OnClickListener, OnViewTapListener, LifecycleObserver {

    @BindView(R.id.tv_shark_description)
    TextView tvSharkDescription;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.pv_expanded_shark)
    PhotoView pvExpandedShark;
    @BindView(R.id.layout_expanded_image_info)
    ConstraintLayout layoutExpandedImageInfo;
    @BindView(R.id.button_download)
    LinearLayout buttonDownload;
    @BindView(R.id.button_flickr)
    LinearLayout buttonFlickr;

    private GlideRequests glideRequests;
    private ImageSaver imageSaver;
    private String flickrUrl;

    @Nullable
    private CloseButtonListener closeButtonListener;
    @Nullable
    private DownloadButtonListener downloadButtonListener;
    @Nullable
    private FlickrButtonListener flickrButtonListener;

    public SharkContentLayout(@NonNull Context context) {
        this(context, null);
    }

    public SharkContentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SharkContentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_expanded_image_layout, this);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        glideRequests = GlideApp.with(this);
        ivClose.setOnClickListener(this);
        pvExpandedShark.setOnViewTapListener(this);
        layoutExpandedImageInfo.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
        buttonFlickr.setOnClickListener(this);
        ((LifecycleOwner) getContext()).getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        closeButtonListener = null;
        downloadButtonListener = null;
        flickrButtonListener = null;
        imageSaver = null;
        glideRequests = null;
    }

    public void updateSharkInfo(PhotoInfoResult photoInfoResult) {
        Timber.d("PhotoInfoResult updated");
        if(photoInfoResult == null) {
            tvSharkDescription.setText("");
            tvUsername.setText("");
        } else {
            PhotoInfo photoInfo = photoInfoResult.getPhotoInfo();
            Owner owner = photoInfo.getOwner();
            tvSharkDescription.setText(photoInfo.getTitle().getContent());
            tvUsername.setText(!owner.getRealname().isEmpty() ? owner.getRealname() : owner.getUsername());
            flickrUrl = photoInfo.getUrls().getUrl().get(0).getContent();
        }
    }

    public void updateSharkPhoto(Photo photo) {
        String url = getSharkUrl(photo);
        // Load the high-resolution "zoomed-in" image.
        glideRequests
                .asBitmap()
                .load(url)
                .into(new CustomViewTarget<PhotoView, Bitmap>(pvExpandedShark) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) { }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageSaver = new ImageSaver(resource, photo.getId());
                        pvExpandedShark.setImageDrawable(new BitmapDrawable(getResources(), resource));
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {
                        imageSaver = null;
                        pvExpandedShark.setImageDrawable(placeholder);
                    }
                });
    }

    public void setCloseButtonListener(@Nullable CloseButtonListener closeButtonListener) {
        this.closeButtonListener = closeButtonListener;
    }

    public void setDownloadButtonListener(@Nullable DownloadButtonListener downloadButtonListener) {
        this.downloadButtonListener = downloadButtonListener;
    }

    public void setFlickrButtonListener(@Nullable FlickrButtonListener flickrButtonListener) {
        this.flickrButtonListener = flickrButtonListener;
    }

    private String getSharkUrl(Photo photo) {
        if(photo.getHeightO() != null) {
            return photo.getUrlO();
        } else if(photo.getUrlL() != null) {
            return photo.getUrlL();
        } else if(photo.getUrlC() != null) {
            return photo.getUrlC();
        } else {
            return photo.getUrlT();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if(closeButtonListener != null) {
                    closeButtonListener.onCloseButtonPress();
                }
                break;
            case R.id.layout_expanded_image_info:
                hideExpandedImageInfo();
                break;
            case R.id.button_download:
                if(downloadButtonListener != null) {
                    downloadButtonListener.onDownloadButtonPress(imageSaver);
                }
                break;
            case R.id.button_flickr:
                if(flickrButtonListener != null) {
                    flickrButtonListener.onFlickrButtonPress(flickrUrl);
                }
                break;
        }
    }

    private void hideExpandedImageInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this);
        }
        boolean visible = layoutExpandedImageInfo.getVisibility() == View.VISIBLE;
        if(visible) {
            layoutExpandedImageInfo.setVisibility(View.GONE);
        } else {
            layoutExpandedImageInfo.setVisibility(View.VISIBLE);
        }
    }

    interface CloseButtonListener {
        void onCloseButtonPress();
    }

    interface DownloadButtonListener {
        void onDownloadButtonPress(ImageSaver imageSaver);
    }

    interface FlickrButtonListener {
        void onFlickrButtonPress(String url);
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        hideExpandedImageInfo();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SHARK_CONTENT_PARCELABLE, super.onSaveInstanceState());
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}