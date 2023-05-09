package com.example.min

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelperImage {
    const val API_KEY = "AIzaSyDgMsj19675xq0E_wVQ8lZ987TA4yd0hCw"
    const val KEY_SEARCH_SYSTEM = "13952549428374f52"
    val baseUrl = "https://www.googleapis.com/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}