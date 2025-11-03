package com.example.ksaturno.checklist

import com.google.gson.annotations.SerializedName

/**
 * MODELO: Representa un único ítem de la lista de verificación.
 * Gson lo usa para convertir el JSON de la API a un objeto de Kotlin.
 */
data class ChecklistItem(
    @SerializedName("id_item") val id: Int,
    @SerializedName("descripcion") val description: String?,
    // El campo 'es_requerido' en la BD es un tinyint (0 o 1).
    // Lo recibiremos como un Int en Kotlin.
    @SerializedName("es_requerido") val isRequired: Int?
)
