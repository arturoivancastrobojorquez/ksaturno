package com.example.ksaturno.servicios

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiciosRepository(private val apiService: ApiService) {

    suspend fun getServicios(): List<Servicio> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getServicios()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("La respuesta del servidor está vacía.")
            } else {
                throw Exception("Error de red al obtener servicios: ${response.code()}")
            }
        }
    }

    suspend fun createServicio(request: CreateServicioRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createServicio(request)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción de red: ${e.message}")
            }
        }
    }

    suspend fun updateServicio(servicio: Servicio): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateServicio(servicio.idServicio, servicio)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (!apiResponse.success && apiResponse.message == null) {
                        apiResponse.copy(message = "La API reportó un error sin mensaje.")
                    } else {
                        apiResponse
                    }
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción de red: ${e.message}")
            }
        }
    }

    suspend fun deleteServicio(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteServicio(ServicioIdBody(id))
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción de red: ${e.message}")
            }
        }
    }
}
