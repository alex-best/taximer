package ru.taximer.taxiandroid.network;

import android.util.Log;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import ru.taximer.taxiandroid.network.models.User;

public class WebService {

    private TaxiApi taxiApi;
    private String token; //TODO сохранить в sharedprefs ну или еще куда

    public WebService(TaxiApi api) {
        this.taxiApi = api;
    }

    public void authUser() {
        taxiApi.authUser(Config.device_hash, Config.device_type).subscribe(new SingleObserver<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(User user) {
                saveToken(user.getApi_token());
            }

            @Override
            public void onError(Throwable e) {
                //что тут делать?
            }
        });
    }

    public void openApp() {
        taxiApi.openApp("Bearer ".concat(token), Config.device_hash, Config.device_type).subscribe(new SingleObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Object o) {
                //а вот тут делать?
                Log.i("OPENAPP_REQUEST", "THANKS");
            }

            @Override
            public void onError(Throwable e) {
                //что тут делать?
                Log.i("OPENAPP_REQUEST", "ERR");
            }
        });
    }

    private void saveToken(String token) {
        this.token = token;
    }
}
