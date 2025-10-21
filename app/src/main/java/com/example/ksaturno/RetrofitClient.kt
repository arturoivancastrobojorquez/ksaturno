package com.example.ksaturno

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL = "http://10.0.2.2/saturno/" // Make sure this ends with a '/'
    private const val BASE_URL = "http://172.16.94.104/saturno/"
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
