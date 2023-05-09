package com.example.min
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AddressApi {
    @GET("/reverse?&format=json&zoom=18")
    suspend fun getAddress(@Query("lat") lat: Double, @Query("lon") lon: Double) : Response<AddressResponse>
}