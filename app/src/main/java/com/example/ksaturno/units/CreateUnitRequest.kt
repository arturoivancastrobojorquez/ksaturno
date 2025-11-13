package com.example.ksaturno.units

import com.google.gson.annotations.SerializedName

data class CreateUnitRequest(
    @SerializedName("id_cliente") val id_cliente: Int,
    @SerializedName("nombre_unidad") val nombre_unidad: String,
    @SerializedName("fecha_instalacion") val fecha_instalacion: String?,
    @SerializedName("tarjeta_sim") val tarjeta_sim: String?,
    @SerializedName("iccid") val iccid: String,
    @SerializedName("idcategoria") val idcategoria: Int,
    @SerializedName("comentarios") val comentarios: String?,
    @SerializedName("estatus") val estatus: String?
)
