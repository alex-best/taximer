package ru.taximer.taxiandroid.presenters.base

import com.arellomobile.mvp.MvpView

interface BaseView: MvpView {
    fun showError(message: String)
}

interface BaseLoadingView: BaseView {
    fun showLoading()
    fun hideLoading()
}