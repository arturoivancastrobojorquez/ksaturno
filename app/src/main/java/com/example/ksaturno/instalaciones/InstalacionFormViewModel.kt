package com.example.ksaturno.instalaciones

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.clients.Client
import com.example.ksaturno.servicios.Servicio
import com.example.ksaturno.servicios.ServiciosRepository
import com.example.ksaturno.technicians.Technician
import com.example.ksaturno.technicians.TechniciansRepository
import com.example.ksaturno.units.UnitsRepository
import kotlinx.coroutines.launch

class InstalacionFormViewModel : ViewModel() {

    private val repository = InstalacionesRepository(RetrofitClient.instance)
    private val serviciosRepository = ServiciosRepository(RetrofitClient.instance)
    private val techniciansRepository = TechniciansRepository(RetrofitClient.instance)
    private val unitsRepository = UnitsRepository(RetrofitClient.instance)

    private val _servicios = MutableLiveData<List<Servicio>>()
    val servicios: LiveData<List<Servicio>> = _servicios

    private val _tecnicos = MutableLiveData<List<Technician>>()
    val tecnicos: LiveData<List<Technician>> = _tecnicos

    private val _selectedClient = MutableLiveData<Client?>()
    val selectedClient: LiveData<Client?> = _selectedClient

    // This will hold the ID of the newly created installation
    private val _newInstallationId = MutableLiveData<Int?>()
    val newInstallationId: LiveData<Int?> = _newInstallationId

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchSpinnerData() {
        viewModelScope.launch {
            try {
                // Load technicians (always needed)
                _tecnicos.postValue(techniciansRepository.getTechnicians())
                
                // Services list is initially empty until a client is selected
                _servicios.postValue(emptyList()) 
                
            } catch (e: Exception) {
                val errorMsg = "Error al cargar datos iniciales: ${e.message}"
                _error.postValue(errorMsg)
                Log.e("InstalacionFormVM", errorMsg, e)
            }
        }
    }

    fun processClientSearchResult(clientId: Int, clientName: String?) {
        val client = Client(clientId, clientName, null, null, null, null) 
        _selectedClient.value = client
        
        loadServicesForClient(clientId)
    }

    private fun loadServicesForClient(clientId: Int) {
        viewModelScope.launch {
            try {
                // Efficiently fetch ONLY the services for this client from the API
                val clientServices = serviciosRepository.getServiciosByClient(clientId)
                _servicios.postValue(clientServices)

            } catch (e: Exception) {
                val errorMsg = "Error al cargar servicios del cliente: ${e.message}"
                _error.postValue(errorMsg)
                Log.e("InstalacionFormVM", errorMsg, e)
            }
        }
    }

    fun createInstalacion(request: CreateInstalacionRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createInstalacion(request)
                if (response.success && response.newId != null) {
                    _newInstallationId.postValue(response.newId)
                } else {
                    _error.postValue(response.message ?: "Error al crear la instalación, no se recibió ID.")
                }
            } catch (e: Exception) {
                val errorMsg = "Excepción al crear instalación: ${e.message}"
                _error.postValue(errorMsg)
                Log.e("InstalacionFormVM", errorMsg, e)
            }
        }
    }

    fun onNavigationComplete() {
        _newInstallationId.value = null
    }
    
    fun clearSelection() {
        _selectedClient.value = null
        _servicios.value = emptyList()
    }
}
