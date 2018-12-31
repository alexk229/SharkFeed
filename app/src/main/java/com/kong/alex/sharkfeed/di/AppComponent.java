package com.kong.alex.sharkfeed.di;

import android.app.Application;

import com.kong.alex.sharkfeed.SharkFeedApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import io.reactivex.disposables.CompositeDisposable;

@Singleton
@Component(
        modules = {
                AndroidInjectionModule.class,
                AppModule.class,
                MainActivityModule.class
        }
)
public interface AppComponent {
        @Component.Builder
        interface Builder {
                @BindsInstance
                Builder application(Application application);

                AppComponent build();
        }

        void inject(SharkFeedApp sharkFeedApp);
}
