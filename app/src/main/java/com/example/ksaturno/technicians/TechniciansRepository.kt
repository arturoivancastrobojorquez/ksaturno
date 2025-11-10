package com.example.ksaturno.technicians

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * El Repositorio (Repository) actúa como el único punto de acceso a los datos de los técnicos.
 * Es el "especialista en datos" en la arquitectura MVVM. Su responsabilidad es abstraer
 * el origen de los datos (en este caso, la red a través de ApiService) del resto de la
 * aplicación, principalmente del ViewModel.
 *
 * @param apiService Una instancia de la interfaz de Retrofit (ApiService) que se utiliza
 *                   para realizar las llamadas de red reales.
 */
class TechniciansRepository(private val apiService: ApiService) {

    /**
     * Obtiene la lista de todos los técnicos desde la API.
     * @return Una lista de objetos 'Technician'.
     * @throws Exception Si la llamada a la red falla o si el cuerpo de la respuesta es nulo,
     *                   lo que indica un error al procesar el JSON (parsing).
     */
    suspend fun getTechnicians(): List<Technician> {
        // withContext(Dispatchers.IO) cambia la ejecución a un hilo de fondo optimizado
        // para operaciones de red y disco, evitando bloquear la interfaz de usuario.
        return withContext(Dispatchers.IO) {
            val response = apiService.getTechnicians()
            if (response.isSuccessful) {
                // Si la respuesta es exitosa pero el cuerpo es nulo, es un error de conversión.
                response.body() ?: throw Exception("Error de parsing: El cuerpo de la respuesta es nulo.")
            } else {
                // Si la llamada HTTP no fue exitosa (ej: error 404, 500), lanza una excepción.
                throw Exception("Error al obtener técnicos: ${response.code()}")
            }
        }
    }

    /**
     * Envía una petición a la API para crear un nuevo técnico.
     * @param request Un objeto 'CreateTechnicianRequest' con los datos del nuevo técnico.
     * @return Un objeto 'ApiResponse' que indica si la operación fue exitosa y un mensaje.
     */
    suspend fun createTechnician(request: CreateTechnicianRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createTechnician(request)
            // Se considera éxito si la llamada HTTP fue exitosa (código 200-299).
            // Esto hace a la app resiliente, incluso si la API no devuelve un JSON de éxito.
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Técnico creado exitosamente")
            } else {
                ApiResponse(false, "Error al crear el técnico")
            }
        }
    }

    /**
     * Envía una petición a la API para actualizar un técnico existente.
     * @param technician El objeto 'Technician' con los datos actualizados.
     * @return Un objeto 'ApiResponse' que indica el resultado de la operación.
     */
    suspend fun updateTechnician(technician: Technician): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateTechnician(technician.idTecnico, technician)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Técnico actualizado exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar el técnico")
            }
        }
    }

    /**
     * Envía una petición a la API para eliminar un técnico.
     * @param id El ID del técnico a eliminar.
     * @return Un objeto 'ApiResponse' que indica el resultado de la operación.
     */
    suspend fun deleteTechnician(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            // Crea un objeto 'TechnicianIdBody' para enviar el ID en el cuerpo de la petición,
            // siguiendo las prácticas de seguridad que establecimos.
            val response = apiService.deleteTechnician(TechnicianIdBody(id))
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Técnico eliminado exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar el técnico")
            }
        }
    }
}
