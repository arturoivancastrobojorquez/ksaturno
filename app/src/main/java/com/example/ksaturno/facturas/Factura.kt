package com.example.ksaturno.facturas

import com.google.gson.annotations.SerializedName

data class Factura(
    @SerializedName("id_factura") val idFactura: Int,
    @SerializedName("numero_factura") val numeroFactura: String,
    @SerializedName("fecha_emision") val fechaEmision: String,
    @SerializedName("monto") val monto: Double,
    @SerializedName("estado") val estado: String?,
    @SerializedName("comentarios") val comentarios: String?,
    @SerializedName("nombre_servicio") val nombreServicio: String?, // Viene del JOIN
    @SerializedName("nombre_unidad") val nombreUnidad: String?      // Viene del JOIN
)
