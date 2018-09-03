package ru.taximer.taxiandroid.network.models

class AvailableTaxiResult {
    var id: Int = 0
    var name: String? = null
    var name_en: String? = null
    var about: String? = null
    var phone_number: String? = null
    var img_url: String? = null
    var rating: Int = 0
    var status: Int = 0
    var isHas_taxi: Boolean = false
    var isHas_carsharing: Boolean = false
    var isHas_child: Boolean = false
    var isPay_card: Boolean = false
    var isPay_card_driver: Boolean = false
    var average_wait_time: Int = 0
}
