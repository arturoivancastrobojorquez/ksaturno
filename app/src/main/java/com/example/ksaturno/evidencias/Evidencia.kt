package com.example.ksaturno.evidencias

import com.google.gson.annotations.SerializedName

data class Evidencia(
    @SerializedName("id_evidencia") val id: Int,
    @SerializedName("id_lista_verificacion") val idListaVerificacion: Int,
    @SerializedName("ruta_archivo") val rutaArchivo: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("fecha_subida") val fechaSubida: String?
)
