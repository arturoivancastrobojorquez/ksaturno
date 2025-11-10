package com.example.ksaturno.checklist

import com.google.gson.annotations.SerializedName

/**
 * MODELO: Define la estructura para ENVIAR los datos de un nuevo ítem.
 */
data class CreateChecklistItemRequest(
    @SerializedName("descripcion") val description: String,
    @SerializedName("es_requerido") val isRequired: Int
)
