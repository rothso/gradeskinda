package com.rothanak.gradeskinda.data.net;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides @Singleton CookieManager provideCookieManager() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return cookieManager;
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(CookieManager cookieManager) {
        OkHttpClient client = new OkHttpClient()
                .setCookieHandler(cookieManager);

        // Log request/response headers and bodies to the console
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(loggingInterceptor);

        return client;
    }

}
