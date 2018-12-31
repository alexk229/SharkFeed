package com.kong.alex.sharkfeed.api;

import com.kong.alex.sharkfeed.api.info.PhotoInfoResult;
import com.kong.alex.sharkfeed.api.search.PhotosResult;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApiService {

    @GET("rest/")
    Single<PhotosResult> getSharkPhotos(
            @Query("method") String method,
            @Query("api_key") String apiKey,
            @Query("text") String text,
            @Query("per_page") Integer perPage,
            @Query("page") Integer page,
            @Query("extras") String extras,
            @Query("format") String format,
            @Query("nojsoncallback") Integer noJsonCallback);

    @GET("rest/")
    Single<PhotoInfoResult> getSharkInfo(
            @Query("method") String method,
            @Query("api_key") String apiKey,
            @Query("photo_id") String photoId,
            @Query("format") String format,
            @Query("nojsoncallback") Integer noJsonCallback);
}
