package com.example.ksaturno.instalaciones

import java.util.Date

/**
 * A data class specifically designed for display purposes in the UI.
 * It holds all the necessary information, already resolved, to show in the installation list.
 */
data class InstalacionDisplay(
    val idInstalacion: Int,
    val fechaInstalacion: Date,
    val estado: String?,
    val componentes: String?,
    val comentarios: String?,
    val nombreServicio: String,
    val nombreUnidad: String,
    val nombreTecnico: String
)
