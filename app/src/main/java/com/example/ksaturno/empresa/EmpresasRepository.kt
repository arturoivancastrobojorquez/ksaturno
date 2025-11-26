package com.example.ksaturno.empresa

import com.example.ksaturno.ApiService
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmpresasRepository(private val apiService: ApiService) {

    suspend fun getEmpresas(): List<Empresa> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getEmpresas()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error fetching empresas: ${response.code()}")
            }
        }
    }

    suspend fun createEmpresa(request: CreateEmpresaRequest): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createEmpresa(request)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción: ${e.message}")
            }
        }
    }

    suspend fun updateEmpresa(empresa: Empresa): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateEmpresa(empresa)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción: ${e.message}")
            }
        }
    }

    suspend fun deleteEmpresa(id: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteEmpresa(EmpresaIdBody(id))
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    ApiResponse(false, "Error de red: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse(false, "Excepción: ${e.message}")
            }
        }
    }
}
