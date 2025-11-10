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
                emptyList()
            }
        }
    }

    suspend fun getUnitsByClient(clientId: Int): List<Unit> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUnitsByClient(clientId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                // Handle error, maybe return an empty list or throw an exception
                emptyList()
            }
        }
    }

    suspend fun createUnit(request: CreateUnitRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createUnit(request)
            if (response.isSuccessful) {
                ApiResponse(true, "Unidad creada exitosamente")
            } else {
                ApiResponse(false, "Error al crear la unidad")
            }
        }
    }

    suspend fun updateUnit(unit: Unit): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateUnit(unit.idUnidad, unit)
            if (response.isSuccessful) {
                ApiResponse(true, "Unidad actualizada exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar la unidad")
            }
        }
    }

    suspend fun deleteUnit(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteUnit(UnitIdBody(id))
            if (response.isSuccessful) {
                ApiResponse(true, "Unidad eliminada exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar la unidad")
            }
        }
    }
}
