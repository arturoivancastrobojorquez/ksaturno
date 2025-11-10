package com.example.ksaturno.servicios

import com.google.gson.annotations.SerializedName

data class ServicioIdBody(
    @SerializedName("id_servicio") val id: Int
)
