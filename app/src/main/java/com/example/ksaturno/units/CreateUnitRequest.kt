package com.example.ksaturno.units

import com.google.gson.annotations.SerializedName

data class CreateUnitRequest(
    @SerializedName("id_cliente") val clientId: Int,
    @SerializedName("nombre_unidad") val name: String,
    @SerializedName("fecha_instalacion") val installDate: String?,
    @SerializedName("ultima_fecha_instalacion") val lastInstallDate: String?,
    @SerializedName("comentarios") val comments: String?,
    @SerializedName("estatus") val status: String?,
    @SerializedName("tarjeta_sim") val simCard: String?,
    @SerializedName("idcategoria") val categoryId: Int
)
