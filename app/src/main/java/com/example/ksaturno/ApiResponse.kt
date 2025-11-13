package com.example.ksaturno

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("id") val newId: Int? = null // Optional field to hold the new ID
)
