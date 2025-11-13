package com.example.ksaturno.evidencias

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.checklist.CompletedChecklistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EvidenceRepository(private val apiService: ApiService) {

    /**
     * Fetches the list of completed checklist items that require evidence for a given installation.
     */
    suspend fun getEvidencePendingItems(installationId: Int): List<CompletedChecklistItem> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getEvidencePendingItems(installationId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error fetching evidence pending items: ${response.code()}")
            }
        }
    }

    /**
     * Saves an evidence record to the database.
     */
    suspend fun saveEvidence(request: CreateEvidenciaRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.saveEvidence(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                ApiResponse(false, "Error de red al guardar evidencia: ${response.code()}")
            }
        }
    }
}
