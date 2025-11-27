package com.example.ksaturno.facturas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.pagos.CreatePaymentRequest
import kotlinx.coroutines.launch

class FacturasViewModel : ViewModel() {

    private val repository = FacturasRepository(RetrofitClient.instance)
    private val apiService = RetrofitClient.instance

    private val _facturas = MutableLiveData<List<Factura>>()
    val facturas: LiveData<List<Factura>> = _facturas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for the report data
    private val _reportData = MutableLiveData<List<Factura>>()
    val reportData: LiveData<List<Factura>> = _reportData
    
    // Payment result
    private val _paymentResult = MutableLiveData<String?>()
    val paymentResult: LiveData<String?> = _paymentResult

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
    
    fun registerPayment(facturaId: Int, clientId: Int, monto: Double, metodo: String, comentarios: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreatePaymentRequest(
                    idFactura = facturaId,
                    idCliente = clientId,
                    monto = monto,
                    metodo = metodo,
                    comentarios = comentarios
                )
                val response = apiService.registerPayment(request)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        _paymentResult.value = apiResponse.message ?: "Pago registrado exitosamente"
                    } else {
                        _error.value = apiResponse?.message ?: "Error al registrar el pago"
                    }
                } else {
                    _error.value = "Error de red: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción al pagar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearPaymentResult() {
        _paymentResult.value = null
    }
}
