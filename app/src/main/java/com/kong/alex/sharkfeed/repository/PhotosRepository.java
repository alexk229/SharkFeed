package com.kong.alex.sharkfeed.repository;

import com.kong.alex.sharkfeed.api.FlickrApiService;
import com.kong.alex.sharkfeed.api.Photos;
import com.kong.alex.sharkfeed.api.PhotosResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class PhotosRepository {
    private final FlickrApiService flickrApiService;

    @Inject
    public PhotosRepository(FlickrApiService flickrApiService) {
        this.flickrApiService = flickrApiService;
    }

    public Observable<PhotosResult> getObserverable() {
        return flickrApiService.getSharkList(
                "flickr.photos.search",
                "949e98778755d1982f537d56236bbb42",
                "shark",
                "json",
                1,
                "url_t"
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
