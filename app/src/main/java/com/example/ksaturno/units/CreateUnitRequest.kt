package com.example.ksaturno.units

import com.google.gson.annotations.SerializedName

data class CreateUnitRequest(
    @SerializedName("id_cliente") val idCliente: Int,
    @SerializedName("nombre_unidad") val nombreUnidad: String,
    @SerializedName("fecha_instalacion") val fechaInstalacion: String?,
    @SerializedName("ultima_fecha_instalacion") val ultimaFechaInstalacion: String?,
    @SerializedName("comentarios") val comentarios: String?,
    @SerializedName("estatus") val estatus: String?,
    @SerializedName("tarjeta_sim") val tarjetaSim: String?,
    @SerializedName("idcategoria") val categoryId: Int
)
