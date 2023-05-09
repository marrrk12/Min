package com.example.min
import com.google.gson.annotations.SerializedName

data class AddressResponse(
    val place_id: String,
    val lat: String,
    val lon: String,
    val display_name: String,
    val address: Address

)