package com.example.ksaturno.instalaciones

import com.google.gson.annotations.SerializedName

data class CreateInstalacionRequest(
    @SerializedName("id_servicio") val id_servicio: Int,
    @SerializedName("id_tecnico") val id_tecnico: Int,
    @SerializedName("fecha_instalacion") val fecha_instalacion: String,
    @SerializedName("componentes_instalados") val componentes_instalados: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("comentarios") val comentarios: String?
)
