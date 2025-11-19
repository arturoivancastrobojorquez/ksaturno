package com.example.ksaturno.servicios

import android.util.Log
import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import com.google.gson.Gson
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

    // NEW: Get services filtered by client directly from the API
    suspend fun getServiciosByClient(clientId: Int): List<Servicio> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getServiciosByClient(clientId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener servicios del cliente: ${response.code()}")
            }
        }
    }

    suspend fun createServicio(request: CreateServicioRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = Gson().toJson(request)
                Log.d("CreateServicio", "Enviando JSON: $jsonBody")

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
                // Log the JSON being sent to the server for debugging
                val jsonBody = Gson().toJson(servicio)
                Log.d("UpdateServicio", "Enviando JSON de actualización: $jsonBody")

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
