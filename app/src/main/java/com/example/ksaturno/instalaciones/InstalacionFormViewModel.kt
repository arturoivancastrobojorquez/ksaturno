package com.example.ksaturno.instalaciones

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.servicios.Servicio
import com.example.ksaturno.technicians.Technician
import kotlinx.coroutines.launch

class InstalacionFormViewModel : ViewModel() {

    private val repository = InstalacionesRepository(RetrofitClient.instance)

    private val _servicios = MutableLiveData<List<Servicio>>()
    val servicios: LiveData<List<Servicio>> = _servicios

    private val _tecnicos = MutableLiveData<List<Technician>>()
    val tecnicos: LiveData<List<Technician>> = _tecnicos

    // This will hold the ID of the newly created installation
    private val _newInstallationId = MutableLiveData<Int?>()
    val newInstallationId: LiveData<Int?> = _newInstallationId

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchSpinnerData() {
        viewModelScope.launch {
            // In a real app, you might want to fetch these from their own repositories
            try {
                _servicios.postValue(RetrofitClient.instance.getServicios().body())
                _tecnicos.postValue(RetrofitClient.instance.getTechnicians().body())
            } catch (e: Exception) {
                _error.postValue("Error al cargar datos: ${e.message}")
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
                Log.e("InstalacionFormVM", "Exception creating installation", e)
                _error.postValue(e.message)
            }
        }
    }

    fun onNavigationComplete() {
        _newInstallationId.value = null
    }
}
