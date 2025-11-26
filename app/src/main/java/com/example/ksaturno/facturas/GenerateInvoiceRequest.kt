package com.example.ksaturno.facturas

import com.google.gson.annotations.SerializedName

data class GenerateInvoiceRequest(
    @SerializedName("id_instalacion") val idInstalacion: Int,
    @SerializedName("id_empresa") val idEmpresa: Int
)
