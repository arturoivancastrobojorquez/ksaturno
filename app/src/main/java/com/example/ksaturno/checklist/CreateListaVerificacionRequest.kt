package com.example.ksaturno.checklist

import com.google.gson.annotations.SerializedName

data class CreateListaVerificacionRequest(
    @SerializedName("id_instalacion") val idInstalacion: Int,
    @SerializedName("id_item") val idItem: Int,
    @SerializedName("verificado") val verificado: Boolean,
    @SerializedName("comentarios") val comentarios: String?
)
