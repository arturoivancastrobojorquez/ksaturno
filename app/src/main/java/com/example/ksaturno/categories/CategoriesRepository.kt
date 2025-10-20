package com.example.ksaturno.categories

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoriesRepository(private val apiService: ApiService) {

    suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener categorías: ${response.code()}")
            }
        }
    }

    suspend fun createCategory(name: String): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createCategory(CreateCategoryRequest(name))
            if (response.isSuccessful) {
                // If the call is successful, assume it worked and provide a standard response.
                ApiResponse(true, response.body()?.message ?: "Categoría creada exitosamente")
            } else {
                ApiResponse(false, "Error al crear la categoría")
            }
        }
    }

    suspend fun updateCategory(id: Int, name: String): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateCategory(id, Category(id, name))
            if (response.isSuccessful) {
                // If the call is successful, assume it worked and provide a standard response.
                ApiResponse(true, response.body()?.message ?: "Categoría actualizada exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar la categoría")
            }
        }
    }

    suspend fun deleteCategory(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteCategory(id)
            if (response.isSuccessful) {
                // If the call is successful, assume it worked and provide a standard response.
                ApiResponse(true, response.body()?.message ?: "Categoría eliminada exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar la categoría")
            }
        }
    }
}
