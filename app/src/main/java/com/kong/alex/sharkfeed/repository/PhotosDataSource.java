package com.kong.alex.sharkfeed.repository;

import com.kong.alex.sharkfeed.NetworkState;
import com.kong.alex.sharkfeed.api.FlickrApiService;
import com.kong.alex.sharkfeed.api.Photo;
import com.kong.alex.sharkfeed.api.PhotosResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class PhotosDataSource extends ItemKeyedDataSource<Integer, Photo> {

    private final FlickrApiService flickrApiService;
    private final CompositeDisposable disposable;
    private int pageNumber = 1;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoadingState;

    @Inject
    public PhotosDataSource(FlickrApiService flickrApiService) {
        this.flickrApiService = flickrApiService;
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

    private Observable<PhotosResult> getObserverable(Integer perPage, Integer page) {
        return flickrApiService.getSharkList(
                "flickr.photos.search",
                "949e98778755d1982f537d56236bbb42",
                "shark",
                perPage,
                page,
                "url_t,url_c,url_l,url_o",
                "json",
                1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Photo> callback) {
        initialLoadingState.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
        disposable.add(getObserverable(params.requestedLoadSize, 1).subscribeWith(getLoadInitialObserver(callback)));
    }

    private DisposableObserver<PhotosResult> getLoadInitialObserver(@NonNull LoadInitialCallback<Photo> callback) {
        return new DisposableObserver<PhotosResult>() {
            @Override
            public void onNext(PhotosResult photosResult) {
                onLoadInitialFetched(photosResult, callback);
            }

            @Override
            public void onError(Throwable e) {
                onLoadInitialError(e);
            }

            @Override
            public void onComplete() {
                Timber.d("onLoadInitialComplete");
            }
        };
    }

    private void onLoadInitialFetched(PhotosResult photosResult, LoadInitialCallback<Photo> callback) {
        initialLoadingState.postValue(NetworkState.LOADED);
        networkState.postValue(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(photosResult.getPhotos().getPhoto());
    }

    private void onLoadInitialError(Throwable e) {
        Timber.e("onLoadInitialError: %s", e.getLocalizedMessage());
        initialLoadingState.postValue(new NetworkState(NetworkState.Status.FAILED, e.getLocalizedMessage()));
        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, e.getLocalizedMessage()));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Photo> callback) {
        networkState.postValue(NetworkState.LOADING);
        Timber.d("NextKey: %s", params.key);
        Timber.d("RequestLoadSize: %s", params.requestedLoadSize);
        disposable.add(getObserverable(params.requestedLoadSize, params.key).subscribeWith(getLoadAfterObserver(params, callback)));
    }

    private DisposableObserver<PhotosResult> getLoadAfterObserver(LoadParams<Integer> params, @NonNull LoadCallback<Photo> callback) {
        return new DisposableObserver<PhotosResult>() {
            @Override
            public void onNext(PhotosResult photosResult) {
                onLoadAfterFetched(photosResult, params, callback);
            }

            @Override
            public void onError(Throwable e) {
                onLoadAfterError(e);
            }

            @Override
            public void onComplete() {
                Timber.d("onLoadAfterCompleted");
            }
        };
    }


    private void onLoadAfterFetched(PhotosResult photosResult, LoadParams<Integer> params, LoadCallback<Photo> callback) {
        networkState.postValue(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(photosResult.getPhotos().getPhoto());
    }

    private void onLoadAfterError(Throwable e) {
        Timber.e(e);
        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, e.getLocalizedMessage()));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Photo> callback) {

    }

    @NonNull
    @Override
    public Integer getKey(@NonNull Photo item) {
        return pageNumber;
    }

    public void clear() {
        pageNumber = 1;
        disposable.clear();
    }
}
