package com.example.ksaturno.instalaciones

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstalacionesRepository(private val apiService: ApiService) {

    suspend fun getInstalaciones(): List<Instalacion> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getInstalaciones()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error fetching instalaciones: ${response.code()}")
            }
        }
    }

    suspend fun createInstalacion(request: CreateInstalacionRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createInstalacion(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                ApiResponse(false, "Error de red: ${response.code()}", null)
            }
        }
    }
    
    // You can add update and delete methods here later if needed.
}
