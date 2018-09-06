package ru.taximer.taxiandroid.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.taximer.taxiandroid.BuildConfig
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.utils.isNotNullOrEmpty
import java.util.concurrent.TimeUnit


///////////////////////////////////////////////////////////////////////////
// Timeout constants
///////////////////////////////////////////////////////////////////////////

const val TIMEOUT_CONNECT = 500L
const val TIMEOUT_READ = 500L
const val TIMEOUT_WRITE = 120L


///////////////////////////////////////////////////////////////////////////
// Default Config Helper
///////////////////////////////////////////////////////////////////////////

fun OkHttpClient.Builder.applyDefaultConfig(): OkHttpClient.Builder = apply {
    connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
    readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }

    addInterceptor { chain ->
        if (Prefs.getToken().isNotNullOrEmpty()) {
            val original = chain.request()
            val request = original.newBuilder()
                    .header("Authorization", "Bearer " + Prefs.getToken())
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }
        else {
            chain.proceed(chain.request())
        }
    }
}
