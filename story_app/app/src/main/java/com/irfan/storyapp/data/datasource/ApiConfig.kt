package com.irfan.storyapp.data.datasource

import com.irfan.storyapp.BuildConfig
import com.irfan.storyapp.common.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(token: String?): ApiService {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val authInterceptor: Interceptor? = token?.let {
                Interceptor { chain ->
                    val req = chain.request()
                    val requestHeaders = req.newBuilder()
                        .addHeader("Authorization", "Bearer $it")
                        .build()
                    chain.proceed(requestHeaders)
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .apply {
                    if (authInterceptor != null) {
                        addInterceptor(authInterceptor)
                    }
                }
                .build()


            val retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}