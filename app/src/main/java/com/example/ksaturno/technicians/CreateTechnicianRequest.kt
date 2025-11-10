package com.example.ksaturno.technicians

import com.google.gson.annotations.SerializedName

/**
 * Define el "plano" o la estructura de datos que se enviará al servidor
 * cuando se quiera crear un nuevo técnico. Esta clase es un modelo de datos
 * que la librería Retrofit utiliza, junto con Gson, para convertir un objeto de Kotlin
 * en una cadena de texto con formato JSON, que es lo que tu API en PHP espera recibir.
 */
data class CreateTechnicianRequest(
    /**
     * @SerializedName("nombre") es una anotación de la librería Gson.
     * Le indica al conversor que, cuando este objeto se transforme en JSON,
     * la propiedad "name" de Kotlin debe llamarse "nombre" en el JSON final.
     * Esto es crucial para que la API en PHP, que espera un campo `nombre`,
     * reciba los datos correctamente.
     *
     * val name: String - Define el nombre del técnico. Es de tipo String y no puede ser nulo.
     */
    @SerializedName("nombre") val name: String,

    /**
     * @SerializedName("direccion") mapea la propiedad "address" de Kotlin al campo "direccion" en el JSON.
     *
     * val address: String? - Define la dirección del técnico. El símbolo "?" significa que
     * este campo es "nullable", es decir, puede ser nulo o no tener valor, coincidiendo
     * con la estructura de la base de datos donde este campo es opcional.
     */
    @SerializedName("direccion") val address: String?,

    /**
     * @SerializedName("telefono") mapea la propiedad "phone" de Kotlin al campo "telefono" en el JSON.
     *
     * val phone: String? - Define el teléfono del técnico. Es nullable (opcional).
     */
    @SerializedName("telefono") val phone: String?,

    /**
     * @SerializedName("correo") mapea la propiedad "email" de Kotlin al campo "correo" en el JSON.
     *
     * val email: String? - Define el correo electrónico del técnico. Es nullable (opcional).
     */
    @SerializedName("correo") val email: String?,

    /**
     * @SerializedName("foto") mapea la propiedad "photo" de Kotlin al campo "foto" en el JSON.
     *
     * val photo: String? - Define la foto del técnico. Se espera que sea una cadena de texto.
     * En nuestro caso, aquí se guarda la imagen codificada en formato Base64.
     * Es nullable porque la foto puede ser opcional al crear un técnico.
     */
    @SerializedName("foto") val photo: String?
)
