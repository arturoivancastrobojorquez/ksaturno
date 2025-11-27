package com.example.ksaturno.pagos

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequest(
    @SerializedName("id_factura") val idFactura: Int,
    @SerializedName("id_cliente") val idCliente: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("metodo") val metodo: String,
    @SerializedName("comentarios") val comentarios: String
)
