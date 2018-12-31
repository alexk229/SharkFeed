package com.kong.alex.sharkfeed.repository.paging;

import com.kong.alex.sharkfeed.network.NetworkState;
import com.kong.alex.sharkfeed.api.search.Photo;
import com.kong.alex.sharkfeed.api.search.PhotosResult;
import com.kong.alex.sharkfeed.repository.SharksRepository;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@Singleton
public class SharksDataSource extends ItemKeyedDataSource<Integer, Photo> {

    private final SharksRepository sharksRepository;
    private final CompositeDisposable disposable;
    private int pageNumber = 1;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoadingState;

    private LoadParams<Integer> loadAfterParams;
    private LoadCallback<Photo> loadAfterCallback;

    public SharksDataSource(SharksRepository sharksRepository, CompositeDisposable disposable) {
        this.sharksRepository = sharksRepository;
        this.disposable = disposable;

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
        Disposable sharksDisposable = sharksRepository.getSharkPhotos(params.requestedLoadSize, pageNumber)
                .doOnSubscribe(__ -> updateLoadInitialState(NetworkState.LOADING))
                .subscribe(sharks -> onLoadInitialFetched(sharks, callback), this::onLoadInitialError);
        disposable.add(sharksDisposable);
    }

    private void updateLoadInitialState(NetworkState state) {
        initialLoadingState.postValue(state);
        networkState.postValue(state);
    }

    private void onLoadInitialFetched(PhotosResult photosResult, LoadInitialCallback<Photo> callback) {
        updateLoadInitialState(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(photosResult.getPhotos().getPhoto());
    }

    private void onLoadInitialError(Throwable e) {
        Timber.e("onLoadInitialError: %s", e.getLocalizedMessage());
        updateLoadInitialState(NetworkState.error(e.getLocalizedMessage()));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Photo> callback) {
        loadAfterParams = params;
        loadAfterCallback = callback;
        Disposable sharksDisposable = sharksRepository.getSharkPhotos(params.requestedLoadSize, params.key)
                .doOnSubscribe(__ -> updateLoadInitialState(NetworkState.LOADING))
                .subscribe(sharks -> onLoadAfterFetched(sharks, params, callback), this::onLoadAfterError);
        disposable.add(sharksDisposable);
    }

    private void updateLoadAfterState(NetworkState state) {
        networkState.postValue(state);
    }

    private void onLoadAfterFetched(PhotosResult photosResult, LoadParams<Integer> params, LoadCallback<Photo> callback) {
        updateLoadAfterState(NetworkState.LOADED);
        pageNumber++;
        callback.onResult(photosResult.getPhotos().getPhoto());
    }

    private void onLoadAfterError(Throwable e) {
        Timber.e("onLoadAfterError: %s", e.getLocalizedMessage());
        updateLoadAfterState(NetworkState.error(e.getLocalizedMessage()));
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
