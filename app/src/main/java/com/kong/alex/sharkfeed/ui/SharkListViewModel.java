package com.kong.alex.sharkfeed.ui;

import com.kong.alex.sharkfeed.network.NetworkState;
import com.kong.alex.sharkfeed.api.info.PhotoInfoResult;
import com.kong.alex.sharkfeed.api.search.Photo;
import com.kong.alex.sharkfeed.repository.paging.SharksDataSource;
import com.kong.alex.sharkfeed.repository.paging.SharksDataSourceFactory;
import com.kong.alex.sharkfeed.repository.SharksRepository;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class SharkListViewModel extends ViewModel {

    private final SharksDataSourceFactory sharksDataSourceFactory;
    private final SharksRepository sharksRepository;
    private LiveData<PagedList<Photo>> sharkList;
    private MutableLiveData<PhotoInfoResult> sharkInfo;
    private MutableLiveData<Photo> currentShark;
    private final CompositeDisposable disposable;

    @Inject
    public SharkListViewModel(SharksRepository sharksRepository, CompositeDisposable disposable) {
        this.sharksRepository = sharksRepository;
        this.disposable = disposable;
        sharkInfo = new MutableLiveData<>();
        currentShark = new MutableLiveData<>();
        sharksDataSourceFactory = new SharksDataSourceFactory(sharksRepository, disposable);
        createPagedList();
    }

    private void createPagedList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(100)
                .setInitialLoadSizeHint(200)
                .setEnablePlaceholders(false)
                .build();
        sharkList = new LivePagedListBuilder<>(sharksDataSourceFactory, config)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(sharksDataSourceFactory.getSharksDataSource(), SharksDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(sharksDataSourceFactory.getSharksDataSource(), SharksDataSource::getInitialLoadingState);
    }

    public MutableLiveData<PhotoInfoResult> getSharkInfoResponse() {
        return sharkInfo;
    }

    public LiveData<PagedList<Photo>> getSharkPhotosResponse() {
        return sharkList;
    }

    public MutableLiveData<Photo> getCurrentShark() {
        return currentShark;
    }

    public void setCurrentShark(Photo photo) {
        currentShark.postValue(photo);
    }

    public void getSharkInfoResponse(String sharkId) {
        Disposable sharkInfoDisposable = sharksRepository.getSharkInfo(sharkId)
                .subscribe(sharkInfo -> this.sharkInfo.postValue(sharkInfo),
                        this::failedSharkInfoResponse);
        disposable.add(sharkInfoDisposable);
    }

    private void failedSharkInfoResponse(Throwable e) {
        Timber.d(e);
    }

    public void refresh() {
        sharksDataSourceFactory.getSharksDataSource().getValue().refresh();
    }

    public void retry() {
        sharksDataSourceFactory.getSharksDataSource().getValue().retry();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        sharksDataSourceFactory.getSharksDataSource().getValue().clear();
    }
}
