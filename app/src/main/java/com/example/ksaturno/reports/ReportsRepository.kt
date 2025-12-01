package com.example.ksaturno.reports

import com.example.ksaturno.ApiService
import com.example.ksaturno.empresa.Empresa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportsRepository(private val apiService: ApiService) {

    suspend fun getReportePagos(): List<ReportePago> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getReportePagos()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        }
    }

    suspend fun getReporteLineasRecuperar(): List<ReporteLineaRecuperar> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getReporteLineasRecuperar()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        }
    }

    suspend fun getReporteLineasSuspendidas(): List<ReporteLineaSuspendida> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getReporteLineasSuspendidas()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        }
    }

    suspend fun getReporteRenovacionesVencidas(): List<ReporteRenovacionVencida> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getReporteRenovacionesVencidas()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        }
    }

    suspend fun getReporteFacturasServicios(): List<ReporteFacturaServicio> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getReporteFacturasServicios()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        }
    }

    // Obtener info de la empresa para el encabezado del PDF
    suspend fun getEmpresaInfo(): Empresa? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmpresas()
                // Asumimos que usamos la primera empresa registrada como la principal
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    response.body()!![0]
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
