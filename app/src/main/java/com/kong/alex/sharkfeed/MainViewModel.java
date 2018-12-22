package com.kong.alex.sharkfeed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    MutableLiveData<Photo> sharkList;
    CompositeDisposable disposable;

    public MainViewModel() {
        sharkList = new MutableLiveData<>();
        disposable = new CompositeDisposable();
    }

    public Observable<Photos> getObserverable() {
        return FlickrClient.getRetrofit().create(FlickrApiServiceInterface.class)
                .getSharkList(
                        Constants.FLICKR_PHOTOS_SEARCH,
                        Constants.FLICKR_API_KEY,
                        "Shark",
                        "json",
                        1,
                        "url_t", "url_m", "url_l", "url_o"
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
