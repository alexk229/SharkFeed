package com.kong.alex.sharkfeed;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface FlickrApiServiceInterface {

    @GET
    Observable<List<Shark>> getSharkList();

}
