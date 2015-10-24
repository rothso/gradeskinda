package com.rothanak.gradeskinda.interactor.scheduler;

import rx.Observable;
import rx.Scheduler;

/**
 * Schedules an Observable to subscribe and observe on defined Schedulers. This Transformer
 * eliminates the need to manually chain {@link Observable#subscribeOn(Scheduler)} or
 * {@link Observable#observeOn(Scheduler)} on every Observable in the interactor layer.
 * <p>
 * This Transformer should be used indirectly by passing an instance of it into
 * {@link Observable#compose(Observable.Transformer)}.
 */
public class AddSchedulesTransformer<T> implements Observable.Transformer<T, T> {

    private final Scheduler subscribeOn;
    private final Scheduler observeOn;

    public AddSchedulesTransformer(Scheduler subscribeOn, Scheduler observeOn) {
        this.subscribeOn = subscribeOn;
        this.observeOn = observeOn;
    }

    @Override public Observable<T> call(Observable<T> observable) {
        return observable.subscribeOn(subscribeOn).observeOn(observeOn);
    }

}