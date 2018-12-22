package com.kong.alex.sharkfeed;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApiServiceInterface {

    @GET("/rest/")
    Observable<Photos> getSharkList(
            @Query("method") String method,
            @Query("api_key") String api_key,
            @Query("text") String text,
            @Query("format") String format,
            @Query("nojsoncallback") Integer callback,
            @Query("extras") String... extras);
}
