package ru.taximer.taxiandroid.network.models

data class SearchTaxiModel(
        val taxopark_id: Long = -1,
        val request_hash: String = "",
        val status: String? = null,
        val taxopark_price: String? = null,
        val order: Int? = null,
        val wait_time: Int? = null,
        val label: String? = null,
        val taxopark: TaxoparkModel,
        val address_from: String? = null,
        val address_to: String? = null,
        val install_link: String? = null,
        val application_link: String? = null,
        var isBest: Boolean = false
)

data class ResultSearchTaxi(
        val request: SearchTaxiModel
)