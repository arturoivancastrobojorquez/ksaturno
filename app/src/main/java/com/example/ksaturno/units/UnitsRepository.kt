package com.example.ksaturno.units

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.categories.Category
import com.example.ksaturno.categories.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnitsRepository(private val apiService: ApiService) {

    private val categoriesRepository = CategoriesRepository(apiService)

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

    suspend fun getCategories(): List<Category> {
        return categoriesRepository.getCategories()
    }

    suspend fun createUnit(unitRequest: CreateUnitRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createUnit(unitRequest)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Unidad creada exitosamente")
            } else {
                ApiResponse(false, "Error al crear la unidad")
            }
        }
    }

    suspend fun updateUnit(unit: Unit): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateUnit(unit.id, unit)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Unidad actualizada exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar la unidad")
            }
        }
    }

    suspend fun deleteUnit(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteUnit(UnitIdBody(id))
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Unidad eliminada exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar la unidad")
            }
        }
    }
}
