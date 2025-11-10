package com.example.ksaturno.instalaciones

import com.google.gson.annotations.SerializedName

data class CreateInstalacionRequest(
    @SerializedName("id_servicio") val idServicio: Int,
    // idUnidad has been removed. The unit is related via the service.
    @SerializedName("id_tecnico") val idTecnico: Int,
    @SerializedName("fecha_instalacion") val fechaInstalacion: String,
    @SerializedName("componentes_instalados") val componentesInstalados: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("comentarios") val comentarios: String?
)
