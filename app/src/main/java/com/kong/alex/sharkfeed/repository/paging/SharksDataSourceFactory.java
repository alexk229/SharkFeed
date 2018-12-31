package com.kong.alex.sharkfeed.repository.paging;

import com.kong.alex.sharkfeed.api.search.Photo;
import com.kong.alex.sharkfeed.repository.SharksRepository;

import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.reactivex.disposables.CompositeDisposable;

@Singleton
public class SharksDataSourceFactory extends DataSource.Factory<Integer, Photo> {

    private MutableLiveData<SharksDataSource> sharksDataSourceLiveData;
    private final SharksRepository sharksRepository;
    private final CompositeDisposable disposable;

    public SharksDataSourceFactory(SharksRepository sharksRepository, CompositeDisposable disposable) {
        this.sharksRepository = sharksRepository;
        this.disposable = disposable;
        sharksDataSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        SharksDataSource sharksDataSource = new SharksDataSource(sharksRepository, disposable);
        sharksDataSourceLiveData.postValue(sharksDataSource);
        return sharksDataSource;
    }

    public LiveData<SharksDataSource> getSharksDataSource() {
        return sharksDataSourceLiveData;
    }
}
