package com.horizam.pro.elean.data.api

import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.PrefManager
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    // online intercepter
    var onlineInterceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val maxAge = 60 // read from cache for 60 seconds even if there is internet connection
            return response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .removeHeader("Pragma")
                .build()
        }
    }
    // offline intercepter
    var offlineInterceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            if (!BaseUtils.isInternetAvailable(App.getAppContext()!!)) {
                val maxStale = 60 * 60 * 24 * 30 // Offline cache available for 30 days
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()
            }
            return chain.proceed(request)
        }
    }
    // Cache size
    var cacheSize = 10 * 1024 * 1024 // 10 MB
    var cache: Cache = Cache(App.getAppContext()!!.cacheDir, cacheSize.toLong())

    private val clientBuilder = OkHttpClient.Builder()
    private val okHttpClient = buildClient()


    private fun buildClient(): OkHttpClient {

        val manager = PrefManager(App.getAppContext()!!)

        clientBuilder.addInterceptor(getLoggingInterceptor())
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${manager.accessToken}")
                    .addHeader("Accept" , "application/json")
                    .addHeader("Device-Id" , BaseUtils.DEVICE_ID)
                    .addHeader("Device-Type" , "android")
                    .build()
                chain.proceed(newRequest)
            }
            .writeTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            /*.addInterceptor(offlineInterceptor)
            .addNetworkInterceptor(onlineInterceptor)
            .cache(cache)*/
        return clientBuilder.build()
    }

    private fun getLoggingInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }


    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("${Constants.BASE_URL}api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}