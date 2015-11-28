package com.rothanak.gradeskinda.domain.interactor;

import com.rothanak.gradeskinda.domain.interactor.scheduler.AddSchedulesTransformer;
import com.rothanak.gradeskinda.domain.service.Authenticator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class InteractorModule {

    @Provides @Singleton AddSchedulesTransformer provideSchedulesTransformer() {
        return new AddSchedulesTransformer(Schedulers.newThread(), AndroidSchedulers.mainThread());
    }

    @Provides @Singleton
    LoginInteractor loginInteractor(Authenticator authenticator, AddSchedulesTransformer scheduler) {
        return new LoginInteractor(authenticator, scheduler);
    }

}
