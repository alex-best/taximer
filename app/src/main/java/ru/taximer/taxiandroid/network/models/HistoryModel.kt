package ru.taximer.taxiandroid.network.models

data class HistoryModel(
        val name: String?,
        val address: String?,
        val coordinates: CoordinateModel

)

data class CoordinateModel(
        val lat: Double,
        val lng: Double
)

data class HistoryResponseModel(
        val from: List<HistoryModel> = emptyList(),
        val to: List<HistoryModel> = emptyList()
)