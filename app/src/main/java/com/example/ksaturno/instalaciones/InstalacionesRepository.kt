package com.example.ksaturno.instalaciones

import com.example.ksaturno.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstalacionesRepository(private val apiService: ApiService) {

    suspend fun getInstalaciones(): List<Instalacion> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getInstalaciones()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                // In a real app, you might want to throw a specific exception here
                emptyList()
            }
        }
    }
    
    // You can add create, update, delete methods here as needed, following the same pattern.
}
