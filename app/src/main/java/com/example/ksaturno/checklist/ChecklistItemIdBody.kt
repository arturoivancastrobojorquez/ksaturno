package com.example.ksaturno.checklist

import com.google.gson.annotations.SerializedName

/**
 * MODELO: Define la estructura para ENVIAR el ID de un ítem a eliminar.
 */
data class ChecklistItemIdBody(@SerializedName("id_item") val id: Int)
