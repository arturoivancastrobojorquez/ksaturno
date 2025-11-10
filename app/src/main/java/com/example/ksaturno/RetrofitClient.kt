package com.example.ksaturno

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Use the domain name as the server may not respond correctly to the IP address.
    private const val BASE_URL = "http://saturnologintech.selfip.com/saturno/"
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
