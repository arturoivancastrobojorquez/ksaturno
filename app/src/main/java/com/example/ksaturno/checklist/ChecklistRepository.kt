package com.example.ksaturno.checklist

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChecklistRepository(private val apiService: ApiService) {

    suspend fun getMasterChecklistItems(): List<ChecklistItem> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getChecklistItems()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error fetching master checklist: ${response.code()}")
            }
        }
    }

    suspend fun createChecklistItem(request: CreateChecklistItemRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createChecklistItem(request)
            if (response.isSuccessful && response.body() != null) response.body()!! else ApiResponse(false, "Error: ${response.code()}")
        }
    }

    suspend fun updateChecklistItem(item: ChecklistItem): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateChecklistItem(item.id, item)
            if (response.isSuccessful && response.body() != null) response.body()!! else ApiResponse(false, "Error: ${response.code()}")
        }
    }

    suspend fun deleteChecklistItem(itemId: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteChecklistItem(ChecklistItemIdBody(itemId))
            if (response.isSuccessful && response.body() != null) response.body()!! else ApiResponse(false, "Error: ${response.code()}")
        }
    }

    suspend fun saveChecklistItemState(request: CreateListaVerificacionRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.saveChecklistItem(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                ApiResponse(false, "Error de red: ${response.code()}", null)
            }
        }
    }
}
