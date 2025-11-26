package com.example.ksaturno.facturas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import kotlinx.coroutines.launch

class FacturasViewModel : ViewModel() {

    private val repository = FacturasRepository(RetrofitClient.instance)

    private val _facturas = MutableLiveData<List<Factura>>()
    val facturas: LiveData<List<Factura>> = _facturas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for the report data
    private val _reportData = MutableLiveData<List<Factura>>()
    val reportData: LiveData<List<Factura>> = _reportData

    fun loadFacturas(clientId: Int, estado: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getFacturasByClient(clientId, estado)
                _facturas.value = result
            } catch (e: Exception) {
                _error.value = "Error al cargar facturas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun prepareReportData(clientId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Fetch ALL invoices to filter properly
                val allFacturas = repository.getFacturasByClient(clientId, "Todos")
                
                // Filter for "pendiente" and "vencido"
                val adeudos = allFacturas.filter { 
                    it.estado?.lowercase() == "pendiente" || it.estado?.lowercase() == "vencido" 
                }
                
                _reportData.value = adeudos
            } catch (e: Exception) {
                _error.value = "Error al generar datos del reporte: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
