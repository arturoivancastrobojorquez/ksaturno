package com.example.ksaturno.technicians

import com.google.gson.annotations.SerializedName

/**
 * Representa el modelo de datos de un Técnico.
 * Esta clase define la estructura de un objeto Técnico tal y como se recibe
 * desde la API (por ejemplo, desde 'tecnicos/listar.php'). La librería Retrofit,
 * junto con Gson, utiliza este "plano" para convertir automáticamente la respuesta
 * JSON del servidor en una lista de objetos 'Technician' de Kotlin.
 * También se usa para enviar un técnico completo al servidor al actualizar.
 */
data class Technician(
    /**
     * @SerializedName("id_tecnico") es una anotación de la librería Gson.
     * Le indica al conversor que, cuando reciba un campo llamado "id_tecnico" en el JSON,
     * su valor debe ser asignado a la propiedad "id" de esta clase.
     * Esto es crucial porque los nombres en la base de datos (con guiones bajos)
     * no siguen las convenciones de nombres de Kotlin (camelCase).
     *
     * val id: Int - El identificador único del técnico. No puede ser nulo.
     */
    @SerializedName("id_tecnico") val id: Int,

    /**
     * @SerializedName("nombre") mapea el campo "nombre" del JSON a la propiedad "name" de Kotlin.
     *
     * val name: String? - El nombre completo del técnico. El símbolo "?" indica que
     * este campo puede ser nulo (nullable), lo que protege la app de errores si
     * la API, por alguna razón, no enviara este campo para algún registro.
     */
    @SerializedName("nombre") val name: String?,

    /**
     * @SerializedName("direccion") mapea el campo "direccion" del JSON a la propiedad "address" de Kotlin.
     *
     * val address: String? - La dirección del técnico. Es nullable (opcional).
     */
    @SerializedName("direccion") val address: String?,

    /**
     * @SerializedName("telefono") mapea el campo "telefono" del JSON a la propiedad "phone" de Kotlin.
     *
     * val phone: String? - El número de teléfono del técnico. Es nullable (opcional).
     */
    @SerializedName("telefono") val phone: String?,

    /**
     * @SerializedName("correo") mapea el campo "correo" del JSON a la propiedad "email" de Kotlin.
     *
     * val email: String? - El correo electrónico del técnico. Es nullable (opcional).
     */
    @SerializedName("correo") val email: String?,

    /**
     * @SerializedName("foto") mapea el campo "foto" del JSON a la propiedad "photo" de Kotlin.
     *
     * val photo: String? - La foto del técnico. Se recibe como una cadena de texto (String)
     * que en nuestro caso es la representación en Base64 de la imagen guardada en la
     * base de datos como un BLOB. Es nullable porque un técnico puede no tener foto.
     */
    @SerializedName("foto") val photo: String? // Assuming Base64 or URL for the blob
)
