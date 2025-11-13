package com.example.ksaturno.servicios

import com.google.gson.annotations.SerializedName

data class CreateServicioRequest(
    @SerializedName("id_unidad") val idUnidad: Int,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("fecha_inicio") val fechaInicio: String,
    @SerializedName("fecha_fin") val fechaFin: String?,
    @SerializedName("fecha_vencimiento") val fechaVencimiento: String?,
    @SerializedName("monto") val monto: Double,
    @SerializedName("estado") val estado: String?,
    @SerializedName("num_periodos") val numPeriodos: Int?,
    @SerializedName("comentarios") val comentarios: String?,
    @SerializedName("id_factura") val idFactura: Int?,
    @SerializedName("periodo_pago") val periodoPago: String,
    @SerializedName("tarjeta_sim") val tarjetaSim: String?,
    @SerializedName("iccid") val iccid: String? // New field
)
