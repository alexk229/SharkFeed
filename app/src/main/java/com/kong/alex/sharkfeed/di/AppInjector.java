package com.kong.alex.sharkfeed.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.kong.alex.sharkfeed.SharkFeedApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Helper class to automatically inject fragments if they implement {@link Injectable}.
 */

public class AppInjector {
    private AppInjector() {
    }

    public static void init(SharkFeedApp sharkFeedApp) {
        sharkFeedApp.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //do nothing
            }

            @Override
            public void onActivityResumed(Activity activity) {
                //do nothing
            }

            @Override
            public void onActivityPaused(Activity activity) {
                //do nothing
            }

            @Override
            public void onActivityStopped(Activity activity) {
                //do nothing
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                //do nothing
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //do nothing
            }
        });
    }

    private static void handleActivity(Activity activity) {
        if(activity instanceof HasSupportFragmentInjector) {
            AndroidInjection.inject(activity);
        }
        if(activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                        @Override
                        public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                            super.onFragmentCreated(fm, f, savedInstanceState);
                            if(f instanceof Injectable) {
                                AndroidSupportInjection.inject(f);
                            }
                        }
                    }, true);
        }
    }
}
