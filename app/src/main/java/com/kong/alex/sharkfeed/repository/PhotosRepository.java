package com.kong.alex.sharkfeed.repository;

import com.kong.alex.sharkfeed.api.FlickrApiService;
import com.kong.alex.sharkfeed.api.PhotosResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PhotosRepository {

    private static final String FLICKR_PHOTOS_SEARCH_METHOD = "flickr.photos.search";
    private static final String FLICKR_API_KEY = "949e98778755d1982f537d56236bbb42";
    private static final String FLICKR_PHOTOS_SEARCH_TEXT = "shark";
    private static final String FLICKR_EXTRAS = "url_t,url_c,url_l,url_o";
    private static final String FLICKR_FORMAT = "json";
    private static final int FLICKR_NO_JSON_CALLBACK = 1;

    private final FlickrApiService flickrApiService;

    @Inject
    public PhotosRepository(FlickrApiService flickrApiService) {
        this.flickrApiService = flickrApiService;
    }

    public Single<PhotosResult> getSharkPhotos(Integer perPage, Integer page) {
        return flickrApiService.getSharkPhotos(
                FLICKR_PHOTOS_SEARCH_METHOD,
                FLICKR_API_KEY,
                FLICKR_PHOTOS_SEARCH_TEXT,
                perPage,
                page,
                FLICKR_EXTRAS,
                FLICKR_FORMAT,
                FLICKR_NO_JSON_CALLBACK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
