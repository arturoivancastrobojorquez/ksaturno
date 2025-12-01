package com.example.ksaturno.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.empresa.Empresa
import kotlinx.coroutines.launch

class ReportsViewModel : ViewModel() {

    private val repository = ReportsRepository(RetrofitClient.instance)

    private val _empresaInfo = MutableLiveData<Empresa?>()
    val empresaInfo: LiveData<Empresa?> = _empresaInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // LiveData específicos para cada reporte
    private val _reportePagos = MutableLiveData<List<ReportePago>>()
    val reportePagos: LiveData<List<ReportePago>> = _reportePagos

    private val _reporteLineasRecuperar = MutableLiveData<List<ReporteLineaRecuperar>>()
    val reporteLineasRecuperar: LiveData<List<ReporteLineaRecuperar>> = _reporteLineasRecuperar

    private val _reporteLineasSuspendidas = MutableLiveData<List<ReporteLineaSuspendida>>()
    val reporteLineasSuspendidas: LiveData<List<ReporteLineaSuspendida>> = _reporteLineasSuspendidas

    private val _reporteRenovacionesVencidas = MutableLiveData<List<ReporteRenovacionVencida>>()
    val reporteRenovacionesVencidas: LiveData<List<ReporteRenovacionVencida>> = _reporteRenovacionesVencidas

    private val _reporteFacturasServicios = MutableLiveData<List<ReporteFacturaServicio>>()
    val reporteFacturasServicios: LiveData<List<ReporteFacturaServicio>> = _reporteFacturasServicios

    init {
        loadEmpresaInfo()
    }

    private fun loadEmpresaInfo() {
        viewModelScope.launch {
            _empresaInfo.value = repository.getEmpresaInfo()
        }
    }

    fun generateReport(type: ReportType) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                when (type) {
                    ReportType.PAGOS -> _reportePagos.value = repository.getReportePagos()
                    ReportType.LINEAS_RECUPERAR -> _reporteLineasRecuperar.value = repository.getReporteLineasRecuperar()
                    ReportType.LINEAS_SUSPENDIDAS -> _reporteLineasSuspendidas.value = repository.getReporteLineasSuspendidas()
                    ReportType.RENOVACIONES_VENCIDAS -> _reporteRenovacionesVencidas.value = repository.getReporteRenovacionesVencidas()
                    ReportType.FACTURAS_SERVICIOS -> _reporteFacturasServicios.value = repository.getReporteFacturasServicios()
                }
            } catch (e: Exception) {
                _error.value = "Error al generar reporte: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

enum class ReportType {
    PAGOS,
    LINEAS_RECUPERAR,
    LINEAS_SUSPENDIDAS,
    RENOVACIONES_VENCIDAS,
    FACTURAS_SERVICIOS
}
