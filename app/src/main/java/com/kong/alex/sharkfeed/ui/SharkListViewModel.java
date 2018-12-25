package com.kong.alex.sharkfeed.ui;

import com.kong.alex.sharkfeed.api.Photo;
import com.kong.alex.sharkfeed.api.Photos;
import com.kong.alex.sharkfeed.api.PhotosResult;
import com.kong.alex.sharkfeed.repository.PhotosRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class SharkListViewModel extends ViewModel {

    private MutableLiveData<List<Photo>> sharkList;
    private CompositeDisposable disposable;
    private PhotosRepository photosRepository;

    @Inject
    public SharkListViewModel(PhotosRepository photosRepository) {
        this.photosRepository = photosRepository;
        sharkList = new MutableLiveData<>();
        disposable = new CompositeDisposable();
    }

    public void getPhotos() {
        disposable.add(photosRepository.getObserverable().subscribeWith(getObserver()));
    }

    public LiveData<List<Photo>> getPhotosResponse() {
        return sharkList;
    }

    private DisposableObserver<PhotosResult> getObserver() {
        return new DisposableObserver<PhotosResult>() {
            @Override
            public void onNext(PhotosResult photosResultResponse) {
                Timber.d("onNext: %s", photosResultResponse.getPhotos().getTotal());
                sharkList.setValue(photosResultResponse.getPhotos().getPhoto());
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Error: %s", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Timber.d("Completed");
            }
        };
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
