package com.kong.alex.sharkfeed.di;

import com.kong.alex.sharkfeed.ui.MainFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    public abstract MainFragment contributeMainFragment();
}
