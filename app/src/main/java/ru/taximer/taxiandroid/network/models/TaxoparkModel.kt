package ru.taximer.taxiandroid.network.models

data class TaxoparkModel(
        val id: Long,
        val name: String? = null,
        val name_en: String? = null,
        val about: String? = null,
        val email: String? = null,
        val phone_number: String? = null,
        val img_url: String? = null
)

data class TaxoparkResultModel(
        val taxoparks: List<TaxoparkModel> = emptyList(),
        val request_hash: String = ""
)