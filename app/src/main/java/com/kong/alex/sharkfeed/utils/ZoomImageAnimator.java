package com.kong.alex.sharkfeed.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


/**
 * https://developer.android.com/training/animation/zoom
 */
public class ZoomImageAnimator {

    private Animator currentAnimator;
    private Rect startBounds;
    private Rect finalBounds;
    private float startScale;

    private final View thumbView;
    private final View container;
    private final View expandedView;
    private final int duration;

    public ZoomImageAnimator(View thumbView, View container, View expandedView, int duration) {
        this.thumbView = thumbView;
        this.container = container;
        this.expandedView = expandedView;
        this.duration = duration;
    }

    private float calculateStartScale() {
        startBounds = new Rect();
        finalBounds = new Rect();
        final Point globalOffset = new Point();
        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        container.getGlobalVisibleRect(finalBounds, globalOffset);
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

        return startScale;
    }

    private void hideThumbView() {
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedView.setVisibility(View.VISIBLE);
        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedView.setPivotX(0f);
        expandedView.setPivotY(0f);
    }

    private void hideExpandedView() {
        thumbView.setAlpha(1f);
        expandedView.setVisibility(View.GONE);
        currentAnimator = null;
    }

    public void zoomImageFromThumb() {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        startScale = calculateStartScale();

        hideThumbView();
        currentAnimator = expandSet();
    }

    public void collapseImageToThumb() {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        startScale = calculateStartScale();

        currentAnimator = collapseSet();
    }

    private AnimatorSet expandSet() {
        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet expandSet = new AnimatorSet();
        expandSet
                .play(ObjectAnimator.ofFloat(expandedView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedView,
                        View.SCALE_Y, startScale, 1f));
        expandSet.setDuration(duration);
        expandSet.setInterpolator(new DecelerateInterpolator());
        expandSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        expandSet.start();
        return expandSet;
    }

    private AnimatorSet collapseSet() {
        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet collapseSet = new AnimatorSet();
        collapseSet.play(ObjectAnimator
                .ofFloat(expandedView, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expandedView,
                                View.Y,startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedView,
                                View.SCALE_X, startScale))
                .with(ObjectAnimator
                        .ofFloat(expandedView,
                                View.SCALE_Y, startScale));
        collapseSet.setDuration(duration);
        collapseSet.setInterpolator(new DecelerateInterpolator());
        collapseSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideExpandedView();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                hideExpandedView();
            }
        });

        collapseSet.start();
        return collapseSet;
    }
}
