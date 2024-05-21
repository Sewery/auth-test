package com.example.firebase_test.core;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirebaseTestAppViewModel extends ViewModel {

    public void singleAction(final String TAG,Action action){
        Completable completable = Completable.fromAction(action)
                .subscribeOn(Schedulers.io());
        Disposable d = completable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.i(TAG, "singleAction, onComplete: single action completed"),
                        throwable ->Log.e(TAG, "singleAction, onError: " + throwable.getLocalizedMessage())
                );
        if(d.isDisposed())
            d.dispose();

    }
    public void singleAction(final String TAG,Action action,Action onComplete){
        Completable completable = Completable.fromAction(action)
                .subscribeOn(Schedulers.io());
        Disposable d = completable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                        onComplete.run();
                        Log.i(TAG, "singleAction, onComplete: action completed");
                    }, throwable -> Log.e(TAG, "singleAction, onError: " + throwable.getLocalizedMessage())
                );
        if(d.isDisposed())
            d.dispose();
    }
    public <T> void singleAction(final String TAG, Single<T> single, Runnable onComplete, Consumer<Throwable> onError){
        Single<T> completable =single
                .subscribeOn(Schedulers.io());
        completable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<T>() {
                               @Override
                               public void onSuccess(@NonNull T t) {
                                   onComplete.run();
                                   Log.i(TAG, "singleAction, onComplete: action completed");
                               }

                               @Override
                               public void onError(@NonNull Throwable e) {
                                   onError.accept(e);
                                   Log.e(TAG, "singleAction, onError: "+e.getLocalizedMessage());
                               }
                           }
                );
    }
    public  void singleAction(final String TAG,Action action,Action onComplete, Consumer<Throwable> onError){
        Completable completable = Completable.fromAction(action)
                .subscribeOn(Schedulers.io());
        Disposable d = completable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                        onComplete.run();
                        Log.i(TAG, "singleAction, onComplete: action completed");
                    }, onError::accept
                );
        if(d.isDisposed())
            d.dispose();
    }

}
