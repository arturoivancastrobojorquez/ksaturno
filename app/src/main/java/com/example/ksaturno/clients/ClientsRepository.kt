package com.example.ksaturno.clients

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientsRepository(private val apiService: ApiService) {

    suspend fun getClients(): List<Client> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getClients()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error de parsing: El cuerpo de la respuesta es nulo.")
            } else {
                throw Exception("Error al obtener clientes: ${response.code()}")
            }
        }
    }

    suspend fun createClient(request: CreateClientRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.createClient(request)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Cliente creado exitosamente")
            } else {
                ApiResponse(false, "Error al crear el cliente")
            }
        }
    }

    suspend fun updateClient(client: Client): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateClient(client.id, client)
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Cliente actualizado exitosamente")
            } else {
                ApiResponse(false, "Error al actualizar el cliente")
            }
        }
    }

    suspend fun deleteClient(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteClient(ClientIdBody(id))
            if (response.isSuccessful) {
                ApiResponse(true, response.body()?.message ?: "Cliente eliminado exitosamente")
            } else {
                ApiResponse(false, "Error al eliminar el cliente")
            }
        }
    }
}
