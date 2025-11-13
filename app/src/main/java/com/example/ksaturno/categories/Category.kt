package com.example.ksaturno.categories

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
) {
    /**
     * This is crucial for the ArrayAdapter in the Spinner.
     * It tells the Spinner to display the value of the 'nombre' property.
     */
    override fun toString(): String {
        return nombre
    }
}
