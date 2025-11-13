package com.example.ksaturno.checklist

import com.google.gson.annotations.SerializedName

data class ListaVerificacionInstalacion(
    @SerializedName("id_lista_verificacion") val id: Int,
    @SerializedName("id_instalacion") val idInstalacion: Int,
    @SerializedName("id_item") val idItem: Int,
    @SerializedName("verificado") val verificado: Boolean,
    @SerializedName("comentarios") val comentarios: String?
)
