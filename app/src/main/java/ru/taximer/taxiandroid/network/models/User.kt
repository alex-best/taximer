package ru.taximer.taxiandroid.network.models

data class User(
    var api_token: String?,
    var id: Int?
)

data class UserResponse(
        val user: User
)
