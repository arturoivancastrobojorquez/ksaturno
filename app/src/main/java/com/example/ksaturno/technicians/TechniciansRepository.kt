package com.example.ksaturno.technicians

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TechniciansRepository(private val apiService: ApiService) {

    suspend fun getTechnicians(): List<Technician> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getTechnicians()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error de parsing: El cuerpo de la respuesta es nulo.")
            } else {
                throw Exception("Error al obtener técnicos: ${response.code()}")
            }
        }
    }

    suspend fun createTechnician(request: CreateTechnicianRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createTechnician(request)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Técnico creado exitosamente")
            } else {
                ApiResponse(false, "Error al crear el técnico")
            }
        }
    }

    suspend fun updateTechnician(technician: Technician): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateTechnician(technician.id, technician)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Técnico actualizado exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar el técnico")
            }
        }
    }

    suspend fun deleteTechnician(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteTechnician(TechnicianIdBody(id))
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Técnico eliminado exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar el técnico")
            }
        }
    }
}
