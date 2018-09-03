package ru.taximer.taxiandroid.network.models

data class BaseResponseModel<T>(
        val result: T?,
        val errors: List<ErrorModel> = emptyList(),
        var warnings: String?,
        val success: Boolean
)