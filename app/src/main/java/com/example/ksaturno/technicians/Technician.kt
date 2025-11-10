package com.example.ksaturno.technicians

import com.google.gson.annotations.SerializedName

data class Technician(
    @SerializedName("id_tecnico") val idTecnico: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("foto") val photo: String?
) {
    override fun toString(): String {
        return nombre
    }
}
