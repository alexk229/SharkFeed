package com.kong.alex.sharkfeed.api;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApiService {

    @GET("rest/")
    Observable<PhotosResult> getSharkList(
            @Query("method") String method,
            @Query("api_key") String apiKey,
            @Query("text") String text,
            @Query("per_page") Integer perPage,
            @Query("page") Integer page,
            @Query("extras") String extras,
            @Query("format") String format,
            @Query("nojsoncallback") Integer noJsonCallback);

//    @GET("rest/")
//    Call<PhotosResult> getSharkList(
//            @Query("method") String method,
//            @Query("api_key") String api_key,
//            @Query("text") String text,
//            @Query("format") String format,
//            @Query("nojsoncallback") Integer noJsonCallback,
//            @Query("extras") String extras);
}
