package com.example.ksaturno.clients

import com.example.ksaturno.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientsRepository(private val apiService: ApiService) {

    suspend fun getClients(): List<Client> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getClients()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener clientes: ${response.code()}")
            }
        }
    }
}
