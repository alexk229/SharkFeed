package com.kong.alex.sharkfeed.di;

import com.kong.alex.sharkfeed.ui.SharkListViewModel;
import com.kong.alex.sharkfeed.viewmodel.SharkFeedViewModelFactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.reactivex.disposables.CompositeDisposable;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SharkListViewModel.class)
    public abstract ViewModel bindSharkListViewModel(SharkListViewModel sharkListViewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(SharkFeedViewModelFactory sharkFeedViewModelFactory);
}
