package com.example.ksaturno.instalaciones

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.clients.Client
import com.example.ksaturno.servicios.Servicio
import com.example.ksaturno.servicios.ServiciosRepository
import com.example.ksaturno.technicians.Technician
import com.example.ksaturno.technicians.TechniciansRepository
import com.example.ksaturno.units.UnitsRepository
import com.example.ksaturno.units.Unit as SaturnoUnit
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

    private val _unitName = MutableLiveData<String>()
    val unitName: LiveData<String> = _unitName

    // This will hold the ID of the newly created installation
    private val _newInstallationId = MutableLiveData<Int?>()
    val newInstallationId: LiveData<Int?> = _newInstallationId

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var loadedUnits: List<SaturnoUnit> = emptyList()
    
    private var _selectedServiceId: Int? = null
    fun getSavedServiceId(): Int? = _selectedServiceId

    fun fetchSpinnerData() {
        viewModelScope.launch {
            try {
                // Load technicians only if not already loaded
                if (_tecnicos.value.isNullOrEmpty()) {
                    _tecnicos.postValue(techniciansRepository.getTechnicians())
                }
                
                // Only clear services if no client is selected
                if (_selectedClient.value == null) {
                    _servicios.postValue(emptyList()) 
                }
                
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
        
        loadDataForClient(clientId)
    }

    private fun loadDataForClient(clientId: Int) {
        viewModelScope.launch {
            try {
                // Efficiently fetch ONLY the services for this client from the API
                val clientServices = serviciosRepository.getServiciosByClient(clientId)
                _servicios.postValue(clientServices)

                // Load units for this client
                loadedUnits = unitsRepository.getUnitsByClient(clientId)

            } catch (e: Exception) {
                val errorMsg = "Error al cargar datos del cliente: ${e.message}"
                _error.postValue(errorMsg)
                Log.e("InstalacionFormVM", errorMsg, e)
            }
        }
    }

    fun onServiceSelected(servicio: Servicio) {
        val unit = loadedUnits.find { it.idUnidad == servicio.idUnidad }
        _unitName.value = unit?.nombreUnidad ?: "Unidad desconocida"
        _selectedServiceId = servicio.idServicio
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
        loadedUnits = emptyList()
        _unitName.value = ""
        _selectedServiceId = null
    }
}
