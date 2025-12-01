package com.example.ksaturno.reports

import com.google.gson.annotations.SerializedName

// Vista: detalle_pagos
data class ReportePago(
    @SerializedName("id_pago") val idPago: Int,
    @SerializedName("cliente") val cliente: String,
    @SerializedName("fecha_pago") val fechaPago: String,
    @SerializedName("monto") val monto: Double,
    @SerializedName("tipo_pago") val tipoPago: String,
    @SerializedName("servicios_cubiertos") val serviciosCubiertos: String?
)

// Vista: lineas_por_recuperar
data class ReporteLineaRecuperar(
    @SerializedName("unidad") val unidad: String,
    @SerializedName("numero_sim") val numeroSim: String?,
    @SerializedName("cliente") val cliente: String,
    @SerializedName("fecha_suspension") val fechaSuspension: String,
    @SerializedName("dias_hasta_recuperacion") val diasHastaRecuperacion: Int
)

// Vista: lineas_suspendidas
data class ReporteLineaSuspendida(
    @SerializedName("unidad") val unidad: String,
    @SerializedName("numero_sim") val numeroSim: String?,
    @SerializedName("cliente") val cliente: String,
    @SerializedName("ultima_fecha_activa") val ultimaFechaActiva: String?
)

// Vista: renovaciones_vencidas
data class ReporteRenovacionVencida(
    @SerializedName("cliente") val cliente: String,
    @SerializedName("unidad") val unidad: String,
    @SerializedName("monto") val monto: Double,
    @SerializedName("fecha_vencimiento") val fechaVencimiento: String?,
    @SerializedName("dias_vencido") val diasVencido: Int? // Si la vista lo calcula o lo calculamos nosotros
)

// Vista: detalle_facturas_servicios
data class ReporteFacturaServicio(
    @SerializedName("numero_factura") val numeroFactura: String?,
    @SerializedName("fecha_emision") val fechaEmision: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("id_servicio") val idServicio: Int
)
