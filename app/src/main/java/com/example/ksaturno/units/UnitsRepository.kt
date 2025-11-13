package com.example.ksaturno.units

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnitsRepository(private val apiService: ApiService) {

    suspend fun getUnits(): List<Unit> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUnits()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener unidades: ${response.code()}")
            }
        }
    }

    suspend fun getUnitsByClient(clientId: Int): List<Unit> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUnitsByClient(clientId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener unidades por cliente: ${response.code()}")
            }
        }
    }

    suspend fun createUnit(request: CreateUnitRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createUnit(request)
                // Now we correctly check the body of the response, not just the HTTP code
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

    suspend fun updateUnit(unit: Unit): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateUnit(unit.idUnidad, unit)
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

    suspend fun deleteUnit(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteUnit(UnitIdBody(id))
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
