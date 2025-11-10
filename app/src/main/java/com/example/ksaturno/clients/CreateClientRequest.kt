package com.example.ksaturno.clients

import com.google.gson.annotations.SerializedName

data class CreateClientRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("email") val email: String,
    @SerializedName("representante") val representante: String
)
