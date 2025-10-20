package com.example.ksaturno.clients

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id_cliente") val id: Int,
    @SerializedName("nombre") val name: String?
)
