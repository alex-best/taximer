package ru.taximer.taxiandroid.network

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import ru.taximer.taxiandroid.network.models.AvailableTaxiResult
import ru.taximer.taxiandroid.network.models.BaseResponseModel
import ru.taximer.taxiandroid.network.models.ResultSearchTaxi
import ru.taximer.taxiandroid.network.models.SupportMsg
import ru.taximer.taxiandroid.network.models.TaxoparkResult
import ru.taximer.taxiandroid.network.models.TaxoparkResultModel
import ru.taximer.taxiandroid.network.models.UserResponse

interface TaxiApi {

    @FormUrlEncoded
    @POST("user/auth")
    fun authUser(
            @Field("device_hash") device_hash: String,
            @Field("device_type") device_type: String
    ): Flowable<BaseResponseModel<UserResponse>>

    @GET("taxopark/get")
    fun getTaxoparks(
            @Query("source_points[lat]") lat: Double,
            @Query("source_points[lng]") lng: Double,
            @Query("destination_points[lat]") dest_lng: Double,
            @Query("destination_points[lng]") dest_lat: Double,
            @Query("address_from") address_from: String,
            @Query("address_to") address_to: String,
            @Query("has_child") has_child: Boolean? = null,
            @Query("pay_cash") pay_cash: Boolean? = null,
            @Query("pay_card") pay_card: Boolean? = null,
            @Query("class_id") class_id: Int? = null
    ): Flowable<BaseResponseModel<TaxoparkResultModel>>

    @GET("search/request")
    fun searchCurrentTaxi(
            @Query("taxopark_id") taxopark_id: Long,
            @Query("request_hash") request_hash: String
    ): Flowable<BaseResponseModel<ResultSearchTaxi>>

    @GET("search/best")
    fun searchBesttTaxi(
            @Query("request_hash") request_hash: String
    ): Flowable<BaseResponseModel<ResultSearchTaxi>>

    @FormUrlEncoded
    @POST("app/open")
    fun openApp(@Header("Authorization") authorization: String, @Field("device_hash") device_hash: String, @Field("device_type") device_type: String): Single<*>

    @FormUrlEncoded
    @POST("app/install")
    fun installApp(@Field("device_hash") device_hash: String, @Field("device_type") device_type: String): ResponseBody


    @POST("support/create")
    fun askSupport(@Body msg: SupportMsg): ResponseBody

    @GET("taxopark/available")
    fun getAvailable(@Header("Authorization") authorization: String,
                     @Query("latitude") latitude: Long,
                     @Query("longitude") longitude: Long
    ): Single<List<AvailableTaxiResult>>

    @GET("taxopark/filter")
    fun getAvailableWithParams(@Header("Authorization") authorization: String,
                               @Query("has_child") has_child: Boolean,
                               @Query("pay_card_driver") pay_card_driver: Boolean,
                               @Query("pay_cash") pay_cash: Boolean,
                               @Query("pay_card") pay_card: Boolean,
                               @Query("class_id") class_id: Int,
                               @Query("volume_id") volume_id: Int,
                               @Query("latitude") latitude: Long,
                               @Query("longitude") longitude: Long
    ): Observable<List<TaxoparkResult>>
}
