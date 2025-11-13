package com.example.ksaturno.servicios

import com.google.gson.annotations.SerializedName

// Dates are Strings to match the API response and avoid parsing errors.
// The conversion to Date objects will be handled in the UI layer when needed.
data class Servicio(
    @SerializedName("id_servicio") val idServicio: Int,
    @SerializedName("id_unidad") val idUnidad: Int,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("fecha_inicio") val fechaInicio: String?,
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
) {
    override fun toString(): String {
        return tipo
    }
}
