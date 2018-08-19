package ru.taximer.taxiandroid.network.usecases

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


///////////////////////////////////////////////////////////////////////////
// Apply Test Delay
///////////////////////////////////////////////////////////////////////////

fun <T> Flowable<T>.applyTestDelay(seconds: Int): Flowable<T> =
        this.delay(seconds.toLong(), java.util.concurrent.TimeUnit.SECONDS)


///////////////////////////////////////////////////////////////////////////
// Apply Default Net Schedulers
///////////////////////////////////////////////////////////////////////////

fun <T> Single<T>.applyDefaultNetSchedulers(): Single<T> = this
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

fun <T> Flowable<T>.applyDefaultNetSchedulers(): Flowable<T> = this
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())