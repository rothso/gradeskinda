package com.rothanak.gradeskinda.domain.interactor.scheduler;

import org.junit.Test;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

public class AddSchedulesTransformerTest {

    public static final Scheduler THIS_THREAD = Schedulers.immediate();
    public static final Scheduler NEW_THREAD = Schedulers.newThread();

    @Test
    public void onCall_SetsSubscribeOn() {
        Scheduler subscribeOn = NEW_THREAD;
        Scheduler observeOn = THIS_THREAD;

        // The transformer directs the Subscriber of the Observable to run on a certain thread
        // (Schedulers.newThread() in this case), so calling Thread.currentThread() within
        // Observable.OnSubscribe will return the aforementioned thread for verification.
        Observable<String> observable = Observable.create((Subscriber<? super String> s) -> {
            s.onNext(Thread.currentThread().getName());
            s.onCompleted();
        }).compose(new AddSchedulesTransformer<>(subscribeOn, observeOn));

        // Verify that the observable did its processing on a new Rx thread
        String subscribingThread = observable.toBlocking().first();
        assertThat(subscribingThread).startsWith("RxNewThreadScheduler");
    }

    @Test
    public void onCall_SetsObserveOn() {
        Scheduler subscribeOn = THIS_THREAD;
        Scheduler observeOn = NEW_THREAD;

        // TestSubscriber allows us to access to the last thread used for observing.
        TestSubscriber<Object> subscriber = TestSubscriber.create();
        Observable.empty()
                .compose(new AddSchedulesTransformer<>(subscribeOn, observeOn))
                .subscribe(subscriber);

        // The observable runs asynchronously, so wait for it to complete
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        // Verify that the observable was observed on a new Rx thread
        String observingThread = subscriber.getLastSeenThread().getName();
        assertThat(observingThread).startsWith("RxNewThreadScheduler");
    }

}