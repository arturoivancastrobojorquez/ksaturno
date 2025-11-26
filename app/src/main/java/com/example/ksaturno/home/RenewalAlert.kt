package com.example.ksaturno.home

import com.google.gson.annotations.SerializedName

data class RenewalAlert(
    @SerializedName("cliente") val cliente: String,
    @SerializedName("unidad") val unidad: String,
    @SerializedName("fecha_inicio_renovacion") val fechaInicio: String,
    @SerializedName("monto") val monto: Double,
    @SerializedName("dias_hasta_vencimiento") val diasRestantes: Int,
    @SerializedName("periodo_pago") val periodoPago: String
)
