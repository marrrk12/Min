package com.example.min

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchImageApi {
    @GET("/customsearch/v1/?key=AIzaSyDgMsj19675xq0E_wVQ8lZ987TA4yd0hCw&cx=13952549428374f52&searchType=image")
    suspend fun getPicture(@Query("q") titlePicture: String) : Response<ImageResponse>
}