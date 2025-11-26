package com.example.ksaturno.home

import com.example.ksaturno.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository {
    private val apiService = RetrofitClient.instance

    suspend fun getRenewalAlerts(): List<RenewalAlert> {
        return withContext(Dispatchers.IO) {
            try {
                // INTENTO DE CARGA REAL (Descomentar cuando el backend y las fechas coincidan)
                /*
                val response = apiService.getRenewalAlerts()
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
                */

                // --- SIMULACIÓN DE DATOS (Basado en tus registros) ---
                // Registro 1: 2025-11-07, pendiente, renovacion, 1000.00
                // Asumiendo que hoy faltan unos días para el 7 de nov
                val alerta1 = RenewalAlert(
                    cliente = "Juan Francisco", // Nombre simulado del cliente ID 2 (o el que corresponda)
                    unidad = "Nissan Versa 2024", // Unidad simulada ID 2
                    fechaInicio = "2025-11-07",
                    monto = 1000.00,
                    diasRestantes = 5, // Simulado: faltan 5 días
                    periodoPago = "mensual"
                )

                // Agrego un segundo ejemplo para que el diseño se vea mejor
                val alerta2 = RenewalAlert(
                    cliente = "Transportes del Norte",
                    unidad = "Camión Hino 500",
                    fechaInicio = "2025-11-15",
                    monto = 2500.00,
                    diasRestantes = 13,
                    periodoPago = "anual"
                )

                listOf(alerta1, alerta2)

            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
