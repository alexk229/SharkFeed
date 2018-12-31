package com.kong.alex.sharkfeed.di;

import android.app.Application;
import android.content.Context;

import com.kong.alex.sharkfeed.common.Constants;
import com.kong.alex.sharkfeed.api.FlickrApiService;
import com.kong.alex.sharkfeed.repository.SharksRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {
        ViewModelModule.class,
        NetworkModule.class
})
public class AppModule {

    @Provides
    @Singleton
    FlickrApiService provideFlickrApiService(
            OkHttpClient okHttpClient,
            GsonConverterFactory gsonConverterFactory,
            RxJava2CallAdapterFactory rxJava2CallAdapterFactory) {

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_FLICKR)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(okHttpClient)
                .build()
                .create(FlickrApiService.class);
    }

    @Provides
    @Singleton
    SharksRepository providePhotosRepository(FlickrApiService flickrApiService) {
        return new SharksRepository(flickrApiService);
    }

    @Provides
    @Singleton
    CompositeDisposable provideDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    Context provideApplication(Application application) {
        return application;
    }

}
