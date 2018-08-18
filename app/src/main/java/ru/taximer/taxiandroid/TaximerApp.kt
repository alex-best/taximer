package ru.taximer.taxiandroid


import android.app.Application

import com.crashlytics.android.answers.Answers
import com.orhanobut.hawk.Hawk

import io.fabric.sdk.android.Fabric


class TaximerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Answers())
        Hawk.init(this)
    }
}
