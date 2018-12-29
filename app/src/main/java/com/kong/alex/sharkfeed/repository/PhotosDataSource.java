package com.kong.alex.sharkfeed.repository;

import com.kong.alex.sharkfeed.NetworkState;
import com.kong.alex.sharkfeed.api.Photo;
import com.kong.alex.sharkfeed.api.PhotosResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@Singleton
public class PhotosDataSource extends ItemKeyedDataSource<Integer, Photo> {

    private final PhotosRepository photosRepository;
    private final CompositeDisposable disposable;
    private int pageNumber = 1;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoadingState;

    private LoadParams<Integer> loadAfterParams;
    private LoadCallback<Photo> loadAfterCallback;

    @Inject
    public PhotosDataSource(PhotosRepository photosRepository) {
        this.photosRepository = photosRepository;
        disposable = new CompositeDisposable();

        networkState = new MutableLiveData<>();

        initialLoadingState = new MutableLiveData<>();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoadingState() {
        return initialLoadingState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Photo> callback) {
        initialLoadingState.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
        Disposable sharksDisposable = photosRepository.getSharkPhotos(params.requestedLoadSize, pageNumber)
                .subscribe(sharks -> onLoadInitialFetched(sharks, callback), this::onLoadInitialError);
        disposable.add(sharksDisposable);
    }

    private void onLoadInitialFetched(PhotosResult photosResult, LoadInitialCallback<Photo> callback) {
        initialLoadingState.postValue(NetworkState.LOADED);
        networkState.postValue(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(photosResult.getPhotos().getPhoto());
        Timber.d("LoadInitialFetched");
    }

    private void onLoadInitialError(Throwable e) {
        Timber.e("onLoadInitialError: %s", e.getLocalizedMessage());
        initialLoadingState.postValue(NetworkState.error(e.getLocalizedMessage()));
        networkState.postValue(NetworkState.error(e.getLocalizedMessage()));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Photo> callback) {
        loadAfterParams = params;
        loadAfterCallback = callback;
        networkState.postValue(NetworkState.LOADING);
        Disposable sharksDisposable = photosRepository.getSharkPhotos(params.requestedLoadSize, params.key)
                .subscribe(sharks -> onLoadAfterFetched(sharks, params, callback), this::onLoadAfterError);
        disposable.add(sharksDisposable);
    }

    private void onLoadAfterFetched(PhotosResult photosResult, LoadParams<Integer> params, LoadCallback<Photo> callback) {
        networkState.postValue(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(photosResult.getPhotos().getPhoto());
        Timber.d("LoadAfterFetched");
    }

    private void onLoadAfterError(Throwable e) {
        Timber.e(e);
        networkState.postValue(NetworkState.error(e.getLocalizedMessage()));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Photo> callback) { }

    @NonNull
    @Override
    public Integer getKey(@NonNull Photo item) {
        return pageNumber;
    }

    public void clear() {
        pageNumber = 1;
        disposable.clear();
    }

    public void refresh() {
        pageNumber = 1;
        invalidate();
    }

    public void retry() {
        if(loadAfterCallback != null) {
            loadAfter(loadAfterParams, loadAfterCallback);
        } else {
            refresh();
        }
    }
}
