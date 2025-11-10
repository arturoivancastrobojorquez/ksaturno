package com.example.ksaturno.instalaciones

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Instalacion(
    @SerializedName("id_instalacion") val idInstalacion: Int,
    @SerializedName("id_servicio") val idServicio: Int,
    // idUnidad has been removed as it's redundant. It can be accessed via the service.
    @SerializedName("id_tecnico") val idTecnico: Int,
    @SerializedName("fecha_instalacion") val fechaInstalacion: Date,
    @SerializedName("componentes_instalados") val componentesInstalados: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("comentarios") val comentarios: String?
)
