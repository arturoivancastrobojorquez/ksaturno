package com.example.ksaturno.technicians

import com.google.gson.annotations.SerializedName

/**
 * Define una estructura de datos muy específica para un propósito único:
 * enviar el ID de un técnico en el CUERPO (body) de una petición DELETE.
 * Siguiendo tu recomendación de seguridad, en lugar de enviar el ID en la URL,
 * lo encapsulamos en un objeto JSON. Esta clase es el "plano" para ese objeto.
 *
 * Ejemplo del JSON que se genera a partir de un objeto de esta clase:
 * { "id_tecnico": 123 }
 */
data class TechnicianIdBody(
    /**
     * @SerializedName("id_tecnico") es una anotación de la librería Gson.
     * Le indica al conversor que la propiedad "id" de esta clase debe llamarse
     * "id_tecnico" cuando se convierta a formato JSON. Esto es fundamental para que
     * tu API de 'eliminar.php', que espera un campo `id_tecnico` en el cuerpo de
     * la petición, pueda recibir y procesar el ID correctamente.
     *
     * val id: Int - El identificador único del técnico que se va a eliminar.
     */
    @SerializedName("id_tecnico") val id: Int
)
