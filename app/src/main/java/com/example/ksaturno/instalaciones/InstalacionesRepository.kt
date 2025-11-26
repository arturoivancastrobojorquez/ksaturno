package com.example.ksaturno.instalaciones

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.empresa.Empresa
import com.example.ksaturno.facturas.GenerateInvoiceRequest
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
    
    suspend fun deleteInstalacion(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteInstalacion(InstalacionIdBody(id))
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}", null)
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción al eliminar: ${e.message}", null)
            }
        }
    }

    suspend fun generateInvoice(request: GenerateInvoiceRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.generateInvoiceFromInstallation(request)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}", null)
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción al generar factura: ${e.message}", null)
            }
        }
    }

    // Helper to get companies for selection dialog
    suspend fun getEmpresas(): List<Empresa> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getEmpresas()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }
}
