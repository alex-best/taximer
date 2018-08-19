package ru.taximer.taxiandroid.network.models

data class BaseResponseModel<T>(
        val result: T,
        val errors: ErrorModel? = null,
        var warnings: String?,
        val success: Boolean
)