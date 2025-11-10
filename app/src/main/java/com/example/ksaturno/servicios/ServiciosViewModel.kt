package com.example.ksaturno.servicios

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.clients.Client
import com.example.ksaturno.clients.ClientsRepository
import com.example.ksaturno.units.Unit
import com.example.ksaturno.units.UnitsRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

class ServiciosViewModel : ViewModel() {

    private val repository = ServiciosRepository(RetrofitClient.instance)
    private val unitsRepository = UnitsRepository(RetrofitClient.instance)
    private val clientsRepository = ClientsRepository(RetrofitClient.instance)

    private val _servicios = MutableLiveData<List<Servicio>>()
    val servicios: LiveData<List<Servicio>> get() = _servicios

    private val _allUnits = MutableLiveData<List<Unit>>()
    private val _allClients = MutableLiveData<List<Client>>()

    private val _selectedClient = MutableLiveData<Client?>()
    val selectedClient: LiveData<Client?> get() = _selectedClient

    private val _filteredUnits = MutableLiveData<List<Unit>>()
    val filteredUnits: LiveData<List<Unit>> get() = _filteredUnits

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    // New LiveData to signal when the save operation is successful
    private val _saveOperationSuccessful = MutableLiveData<Boolean>()
    val saveOperationSuccessful: LiveData<Boolean> get() = _saveOperationSuccessful

    init {
        fetchServicios()
        fetchAllClients()
        fetchAllUnits()
    }

    private fun fetchAllClients() = viewModelScope.launch {
        try { _allClients.value = clientsRepository.getClients() } catch (e: Exception) { handleError("Error al cargar clientes", e) }
    }

    private fun fetchAllUnits() = viewModelScope.launch {
        try { _allUnits.value = unitsRepository.getUnits() } catch (e: Exception) { handleError("Error al cargar unidades", e) }
    }

    fun fetchServicios() = viewModelScope.launch {
        try { _servicios.value = repository.getServicios() } catch (e: Exception) { handleError("Error al cargar servicios", e) }
    }

    fun processClientSearchResult(clientId: Int) {
        selectClient(_allClients.value?.find { it.id == clientId })
    }

    fun prepareForEdit(servicio: Servicio) {
        val unit = _allUnits.value?.find { it.idUnidad == servicio.idUnidad }
        selectClient(unit?.let { u -> _allClients.value?.find { c -> c.id == u.idCliente } })
    }

    private fun selectClient(client: Client?) {
        _selectedClient.value = client
        _filteredUnits.value = if (client != null) _allUnits.value?.filter { it.idCliente == client.id } else emptyList()
    }

    fun clearSelection() {
        selectClient(null)
    }

    fun onSaveOperationComplete() {
        _saveOperationSuccessful.value = false // Reset the signal
    }

    fun createServicio(request: CreateServicioRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createServicio(request)
                if (response.success) {
                    fetchServicios()
                    _toastMessage.value = response.message
                    _saveOperationSuccessful.value = true // Signal success
                } else {
                    _toastMessage.value = "Error al crear: ${response.message}"
                }
            } catch (e: Exception) {
                handleError("Excepción al crear servicio", e)
            }
        }
    }

    fun updateServicio(servicio: Servicio) {
        viewModelScope.launch {
            try {
                val response = repository.updateServicio(servicio)
                if (response.success) {
                    fetchServicios()
                    _toastMessage.value = response.message
                    _saveOperationSuccessful.value = true // Signal success
                } else {
                    _toastMessage.value = "Error al actualizar: ${response.message}"
                }
            } catch (e: Exception) {
                handleError("Excepción al actualizar servicio", e)
            }
        }
    }
    
    // ... (deleteServicio, calculateNumberOfPeriods, and handleError remain the same)
    fun deleteServicio(servicio: Servicio) = viewModelScope.launch { try { repository.deleteServicio(servicio.idServicio).also { if(it.success) fetchServicios() ; _toastMessage.value = it.message } } catch(e: Exception) { handleError("Excepción al eliminar", e) } }
    fun calculateNumberOfPeriods(startDate: Date, endDate: Date, paymentPeriod: String): Int {
        val startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        if (endLocalDate.isBefore(startLocalDate)) return 0
        return when (paymentPeriod) {
            "mensual" -> ChronoUnit.MONTHS.between(startLocalDate, endLocalDate).toInt()
            "bimestral" -> (ChronoUnit.MONTHS.between(startLocalDate, endLocalDate) / 2).toInt()
            "semestral" -> (ChronoUnit.MONTHS.between(startLocalDate, endLocalDate) / 6).toInt()
            "anual" -> ChronoUnit.YEARS.between(startLocalDate, endLocalDate).toInt()
            else -> 0
        }
    }
    private fun handleError(message: String, e: Exception) { val errorMsg = "$message: ${e.message}"; _toastMessage.value = errorMsg; Log.e("ServiciosViewModel", errorMsg, e) }
}
