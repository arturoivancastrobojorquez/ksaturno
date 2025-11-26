package com.example.ksaturno.empresa

import com.google.gson.annotations.SerializedName

data class Empresa(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("rfc") val rfc: String,
    @SerializedName("representante") val representante: String,
    @SerializedName("folio_factura") val folioFactura: Int
)
