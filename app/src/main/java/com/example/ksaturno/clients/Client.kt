package com.example.ksaturno.clients

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id_cliente") val id: Int,
    @SerializedName("nombre") val name: String?,
    @SerializedName("direccion") val address: String?,
    @SerializedName("telefono") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("representante") val representative: String?
)
