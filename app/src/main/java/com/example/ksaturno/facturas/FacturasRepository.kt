package com.example.ksaturno.facturas

import com.example.ksaturno.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FacturasRepository(private val apiService: ApiService) {

    suspend fun getFacturasByClient(clientId: Int, estado: String): List<Factura> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFacturasByClient(clientId, estado)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList() // O manejar error de otra forma
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
