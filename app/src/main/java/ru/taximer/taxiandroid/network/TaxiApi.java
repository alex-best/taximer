package ru.taximer.taxiandroid.network;

import java.util.List;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.taximer.taxiandroid.network.models.AvailableTaxiResult;
import ru.taximer.taxiandroid.network.models.SearchTaxiResult;
import ru.taximer.taxiandroid.network.models.SupportMsg;
import ru.taximer.taxiandroid.network.models.TaxoparkResult;
import ru.taximer.taxiandroid.network.models.User;

public interface TaxiApi {

    //TODO add return type with token
    @FormUrlEncoded
    @POST("user/auth")
    Single<User> authUser(@Field("device_hash") String device_hash, @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("app/open")
    Single openApp(@Header("Authorization") String authorization, @Field("device_hash") String device_hash, @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("app/install")
    ResponseBody installApp(@Field("device_hash") String device_hash, @Field("device_type") String device_type);


    @POST("support/create")
    ResponseBody askSupport(@Body SupportMsg msg);

    @GET("taxopark/available")
    Single<List<AvailableTaxiResult>> getAvailable(@Header("Authorization") String authorization,
                                                   @Query("latitude") long latitude, @Query("longitude") long longitude);

    @GET("taxopark/filter")
    Observable<List<TaxoparkResult>> getAvailableWithParams(@Header("Authorization") String authorization,
                                                            @Query("has_child") boolean has_child, @Query("pay_card_driver") boolean pay_card_driver,
                                                            @Query("pay_cash") boolean pay_cash, @Query("pay_card") boolean pay_card,
                                                            @Query("class_id") int class_id, @Query("volume_id") int volume_id,
                                                            @Query("latitude") long latitude, @Query("longitude") long longitude);

    @GET("search/request")
    Observable<SearchTaxiResult> searchCurrentTaxi(@Query("taxopark_id") int taxopark_id, @Query("source_points[lat]") long lat, @Query("source_points[lng]") long lng,
                                                   @Query("destination_points[lng]") long dest_lng, @Query("destination_points[lat]") long dest_lat,
                                                   @Query("locality_id") int locality_id, @Query("pay_card") int pay_card, @Query("request_hash") String request_hash);
}
