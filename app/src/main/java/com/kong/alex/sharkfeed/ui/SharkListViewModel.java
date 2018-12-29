package com.kong.alex.sharkfeed.ui;

import com.kong.alex.sharkfeed.NetworkState;
import com.kong.alex.sharkfeed.api.Photo;
import com.kong.alex.sharkfeed.repository.PhotosDataSource;
import com.kong.alex.sharkfeed.repository.PhotosDataSourceFactory;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class SharkListViewModel extends ViewModel {

    private final PhotosDataSourceFactory photosDataSourceFactory;
    private LiveData<PagedList<Photo>> sharkList;

    @Inject
    public SharkListViewModel(PhotosDataSourceFactory photosDataSourceFactory) {
        this.photosDataSourceFactory = photosDataSourceFactory;
        createPagedList();
    }

    private void createPagedList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(100)
                .setInitialLoadSizeHint(200)
                .setEnablePlaceholders(false)
                .build();
        sharkList = new LivePagedListBuilder<>(photosDataSourceFactory, config)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(photosDataSourceFactory.getPhotosDataSource(), PhotosDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(photosDataSourceFactory.getPhotosDataSource(), PhotosDataSource::getInitialLoadingState);
    }

    public LiveData<PagedList<Photo>> getPhotosResponse() {
        return sharkList;
    }

    public void refresh() {
        photosDataSourceFactory.getPhotosDataSource().getValue().refresh();
    }

    public void retry() {
        photosDataSourceFactory.getPhotosDataSource().getValue().retry();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        photosDataSourceFactory.getPhotosDataSource().getValue().clear();
    }
}
