package com.rothanak.gradeskinda.data.net;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RxJavaCallAdapterFactory;

@Module
public class NetworkModule {

    @Provides @Singleton RxJavaCallAdapterFactory provideCallAdapter() {
        // For Retrofit 2.0 to return Observable<Foo> instances
        return RxJavaCallAdapterFactory.create();
    }

    @Provides @Singleton CookieManager provideCookieManager() {
        return new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(CookieManager cookieManager) {
        OkHttpClient client = new OkHttpClient()
                .setCookieHandler(cookieManager);

        // todo instead look into retrying with exponential backoff?
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setConnectTimeout(30, TimeUnit.SECONDS);

        // Log request/response headers and bodies to the console
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(loggingInterceptor);

        return client;
    }

}
