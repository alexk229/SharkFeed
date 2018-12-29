package com.kong.alex.sharkfeed.repository;

import com.kong.alex.sharkfeed.api.FlickrApiService;
import com.kong.alex.sharkfeed.api.Photo;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import timber.log.Timber;

@Singleton
public class PhotosDataSourceFactory extends DataSource.Factory<Integer, Photo> {

    private MutableLiveData<PhotosDataSource> photosDataSourceLiveData;
    private final PhotosRepository photosRepository;

    @Inject
    public PhotosDataSourceFactory(PhotosRepository photosRepository) {
        this.photosRepository = photosRepository;
        photosDataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        Timber.d("DataSource Created");
        PhotosDataSource photosDataSource = new PhotosDataSource(photosRepository);
        photosDataSourceLiveData.postValue(photosDataSource);
        return photosDataSource;
    }

    public LiveData<PhotosDataSource> getPhotosDataSource() {
        return photosDataSourceLiveData;
    }
}
