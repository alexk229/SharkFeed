package com.kong.alex.sharkfeed.di;

import com.kong.alex.sharkfeed.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    public abstract MainActivity contributeMainActivity();
}
