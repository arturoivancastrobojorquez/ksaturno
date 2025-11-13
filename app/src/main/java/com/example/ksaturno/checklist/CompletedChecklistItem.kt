package com.example.ksaturno.checklist

import com.google.gson.annotations.SerializedName

/**
 * Represents a checklist item that has been verified for a specific installation
 * and requires evidence.
 */
data class CompletedChecklistItem(
    @SerializedName("id_lista_verificacion") val idListaVerificacion: Int,
    @SerializedName("id_instalacion") val idInstalacion: Int,
    @SerializedName("id_item") val idItem: Int,
    @SerializedName("descripcion") val descripcion: String
)
