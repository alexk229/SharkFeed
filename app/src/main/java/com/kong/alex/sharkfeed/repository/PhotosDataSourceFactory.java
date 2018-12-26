package com.kong.alex.sharkfeed.repository;

import com.kong.alex.sharkfeed.api.Photo;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

@Singleton
public class PhotosDataSourceFactory extends DataSource.Factory<Integer, Photo> {

    private final PhotosDataSource photosDataSource;
    private MutableLiveData<PhotosDataSource> mutableLiveData;

    @Inject
    public PhotosDataSourceFactory(PhotosDataSource photosDataSource) {
        this.photosDataSource = photosDataSource;
        mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        mutableLiveData.postValue(photosDataSource);
        return photosDataSource;
    }

    @NonNull
    public PhotosDataSource getPhotosDataSource() {
        return photosDataSource;
    }
}
