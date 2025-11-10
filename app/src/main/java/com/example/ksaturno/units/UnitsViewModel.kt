package com.example.ksaturno.units

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.clients.Client
import com.example.ksaturno.clients.ClientsRepository
import kotlinx.coroutines.launch

class UnitsViewModel : ViewModel() {

    private val _units = MutableLiveData<List<Unit>>()
    val units: LiveData<List<Unit>> get() = _units

    private val _clients = MutableLiveData<List<Client>>()
    val clients: LiveData<List<Client>> get() = _clients

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val repository = UnitsRepository(RetrofitClient.instance)
    private val clientsRepository = ClientsRepository(RetrofitClient.instance)

    init {
        fetchUnits()
        fetchClients()
    }

    private fun fetchUnits() {
        viewModelScope.launch {
            try {
                _units.value = repository.getUnits()
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar unidades"
            }
        }
    }

    private fun fetchClients() {
        viewModelScope.launch {
            try {
                _clients.value = clientsRepository.getClients()
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar clientes"
            }
        }
    }

    fun createUnit(unit: CreateUnitRequest) {
        viewModelScope.launch {
            val response = repository.createUnit(unit)
            if (response.success) {
                fetchUnits()
                _toastMessage.value = "Unidad creada"
            } else {
                _toastMessage.value = "Error al crear unidad"
            }
        }
    }

    fun updateUnit(unit: Unit) {
        viewModelScope.launch {
            val response = repository.updateUnit(unit)
            if (response.success) {
                fetchUnits()
                _toastMessage.value = "Unidad actualizada"
            } else {
                _toastMessage.value = "Error al actualizar unidad"
            }
        }
    }

    fun deleteUnit(unitId: Int) {
        viewModelScope.launch {
            val response = repository.deleteUnit(unitId)
            if (response.success) {
                fetchUnits()
                _toastMessage.value = "Unidad eliminada"
            } else {
                _toastMessage.value = "Error al eliminar unidad"
            }
        }
    }

    fun findClientPosition(clientId: Int): Int {
        return _clients.value?.indexOfFirst { it.id == clientId } ?: -1
    }
}
