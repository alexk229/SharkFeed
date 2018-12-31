package com.kong.alex.sharkfeed.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.kong.alex.sharkfeed.GlideApp;
import com.kong.alex.sharkfeed.GlideRequests;
import com.kong.alex.sharkfeed.utils.ImageSaver;
import com.kong.alex.sharkfeed.NetworkState;
import com.kong.alex.sharkfeed.api.info.Owner;
import com.kong.alex.sharkfeed.api.info.PhotoInfo;
import com.kong.alex.sharkfeed.api.info.PhotoInfoResult;
import com.kong.alex.sharkfeed.api.search.Photo;
import com.kong.alex.sharkfeed.di.Injectable;
import com.kong.alex.sharkfeed.R;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class MainFragment extends Fragment implements Injectable, RetryCallback, SharkClickListener, View.OnClickListener {

    @BindView(R.id.rv_sharks)
    RecyclerView rvSharks;
    @BindView(R.id.swipe_refresh_sharks)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.tv_shark_description)
    TextView tvSharkDescription;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.pv_expanded_shark)
    PhotoView pvExpandedShark;
    @BindView(R.id.layout_expanded_image)
    ConstraintLayout layoutExpandedImage;
    @BindView(R.id.layout_expanded_image_info)
    ConstraintLayout layoutExpandedImageInfo;
    @BindView(R.id.button_download)
    LinearLayout buttonDownload;
    @BindView(R.id.button_flickr)
    LinearLayout buttonFlickr;
    @BindInt(android.R.integer.config_shortAnimTime)
    int mShortAnimationDuration;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Context context;

    private SharksAdapter sharksAdapter;
    private SharkListViewModel sharkListViewModel;

    private View rootView;
    private GlideRequests glideRequest;
    private Animator mCurrentAnimator;
    private ImageSaver imageSaver;


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
        // Retrieve and cache the system's default "short" animation time.
        pvExpandedShark.setOnViewTapListener((view, x, y) -> hideExpandedImageInfo());
        layoutExpandedImageInfo.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
        buttonFlickr.setOnClickListener(this);
        sharkListViewModel.getSharkInfoResponse().observe(this, this::setZoomedImageContent);
        sharkListViewModel.getCurrentSelectedShark().observe(this, this::setZoomedImage);
    }

    private void hideExpandedImageInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(layoutExpandedImage);
        }
        boolean visible = layoutExpandedImageInfo.getVisibility() == View.VISIBLE;
        if(visible) {
            layoutExpandedImageInfo.setVisibility(View.GONE);
        } else {
            layoutExpandedImageInfo.setVisibility(View.VISIBLE);
        }
    }

    private void setZoomedImageContent(@Nullable PhotoInfoResult photoInfoResult) {
        Timber.d("PhotoInfoResult updated");
        if(photoInfoResult == null) {
            tvSharkDescription.setText("");
            tvUsername.setText("");
        } else {
            PhotoInfo photoInfoInfo = photoInfoResult.getPhotoInfo();
            Owner owner = photoInfoInfo.getOwner();
            tvSharkDescription.setText(photoInfoInfo.getTitle().getContent());
            tvUsername.setText(!owner.getRealname().isEmpty() ? owner.getRealname() : owner.getUsername());
        }
    }

    private void setZoomedImage(Photo photo) {
        String url = getSharkUrl(photo);
        // Load the high-resolution "zoomed-in" image.
        glideRequest
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
    public void onClick(View view, Photo photo) {
        sharkListViewModel.getSharkInfoResponse(photo.getId());
        sharkListViewModel.setCurrentSelectedShark(photo);
        zoomImageFromThumb(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_download:
                String savedImageLocation = imageSaver.saveImage();
                Toast.makeText(context, "Saved: " + savedImageLocation, Toast.LENGTH_LONG).show();
                addPicToGallery(savedImageLocation);
                break;
            case R.id.button_flickr:
                break;
            case R.id.layout_expanded_image_info:
                hideExpandedImageInfo();
                break;
            case R.id.iv_close:
                break;
        }
    }

    private void addPicToGallery(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imagePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
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

    /**
     * zoomImageFromThumb found in link below
     * https://developer.android.com/training/animation/zoom
     */
    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        rootView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        layoutExpandedImage.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        layoutExpandedImage.setPivotX(0f);
        layoutExpandedImage.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(layoutExpandedImage, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(layoutExpandedImage, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(layoutExpandedImage, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(layoutExpandedImage,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        ivClose.setOnClickListener(view -> {
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(layoutExpandedImage, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(layoutExpandedImage,
                                    View.Y,startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(layoutExpandedImage,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(layoutExpandedImage,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(mShortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    layoutExpandedImage.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    layoutExpandedImage.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }
            });
            set1.start();
            mCurrentAnimator = set1;
        });
    }
}
