package ru.taximer.taxiandroid.network

import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import ru.taximer.taxiandroid.network.models.AvailableTaxiResult
import ru.taximer.taxiandroid.network.models.BaseResponseModel
import ru.taximer.taxiandroid.network.models.HistoryResponseModel
import ru.taximer.taxiandroid.network.models.ResultSearchTaxi
import ru.taximer.taxiandroid.network.models.TaxoparkResultModel
import ru.taximer.taxiandroid.network.models.UserResponse

interface TaxiApi {

    @FormUrlEncoded
    @POST("user/auth")
    fun authUser(
            @Field("device_hash") device_hash: String,
            @Field("device_type") device_type: String
    ): Flowable<BaseResponseModel<UserResponse>>

    @FormUrlEncoded
    @POST("user/register-push")
    fun sendPush(
            @Field("token") token: String
    ): Flowable<BaseResponseModel<Any?>>

    @FormUrlEncoded
    @POST("user/notification")
    fun changeNotifications(
            @Field("active") active: Boolean
    ): Flowable<BaseResponseModel<Any?>>

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

    @GET("user/adresses?lat=59.98597928342742&lng=30.320876240830824")
    fun getHistory(
            @Query("lat") lat: Double,
            @Query("lng") lng: Double
    ): Flowable<BaseResponseModel<HistoryResponseModel>>

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


    @FormUrlEncoded
    @POST("user/support")
    fun askSupport(
            @Field("text") text: String,
            @Field("email") email: String
    ): Flowable<BaseResponseModel<Any?>>

    @GET("taxopark/available")
    fun getAvailable(@Header("Authorization") authorization: String,
                     @Query("latitude") latitude: Long,
                     @Query("longitude") longitude: Long
    ): Single<List<AvailableTaxiResult>>
}
