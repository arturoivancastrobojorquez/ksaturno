package com.example.ksaturno

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("user")
    val user: User?, // This can be null if login fails or user data isn't sent

    @SerializedName("message")
    val message: String? // This can be null if no message is sent
)

data class User(
    @SerializedName("id")
    val id: String, // Assuming ID is a string, adjust to Int if it's a number

    @SerializedName("username")
    val username: String
)
