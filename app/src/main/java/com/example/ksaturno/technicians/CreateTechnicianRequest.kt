package com.example.ksaturno.technicians

import com.google.gson.annotations.SerializedName

data class CreateTechnicianRequest(
    @SerializedName("nombre") val name: String,
    @SerializedName("direccion") val address: String?,
    @SerializedName("telefono") val phone: String?,
    @SerializedName("correo") val email: String?,
    @SerializedName("foto") val photo: String? // Assuming Base64 or URL
)
