package com.example.ksaturno.checklist

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * REPOSITORIO: Abstrae el origen de los datos de la lista de verificación.
 * Es el único que sabe que los datos vienen de una API remota (ApiService).
 */
class ChecklistRepository(private val apiService: ApiService) {

    suspend fun getChecklistItems(): List<ChecklistItem> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getChecklistItems()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error de parsing: El cuerpo de la respuesta es nulo.")
            } else {
                throw Exception("Error al obtener los ítems: ${response.code()}")
            }
        }
    }

    suspend fun createChecklistItem(request: CreateChecklistItemRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createChecklistItem(request)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Ítem creado exitosamente")
            } else {
                ApiResponse(false, "Error al crear el ítem")
            }
        }
    }

    suspend fun updateChecklistItem(item: ChecklistItem): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateChecklistItem(item.id, item)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Ítem actualizado exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar el ítem")
            }
        }
    }

    suspend fun deleteChecklistItem(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteChecklistItem(ChecklistItemIdBody(id))
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Ítem eliminado exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar el ítem")
            }
        }
    }
}
