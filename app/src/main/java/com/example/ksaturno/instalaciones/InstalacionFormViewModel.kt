package com.example.ksaturno.instalaciones

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

    // The LiveData for Units has been removed.

    private val _servicios = MutableLiveData<List<Servicio>>()
    val servicios: LiveData<List<Servicio>> = _servicios

    private val _tecnicos = MutableLiveData<List<Technician>>()
    val tecnicos: LiveData<List<Technician>> = _tecnicos

    private val _saveResult = MutableLiveData<ApiResponse>()
    val saveResult: LiveData<ApiResponse> = _saveResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchSpinnerData() {
        viewModelScope.launch {
            try {
                // The call to getUnits() has been removed.
                _servicios.postValue(RetrofitClient.instance.getServicios().body())
                _tecnicos.postValue(RetrofitClient.instance.getTechnicians().body())
            } catch (e: Exception) {
                _error.postValue("Error al cargar datos para los spinners: ${e.message}")
            }
        }
    }

    fun saveInstalacion(request: CreateInstalacionRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.createInstalacion(request)
                if (response.isSuccessful) {
                    _saveResult.postValue(response.body())
                } else {
                    _error.postValue("Error al guardar la instalación: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Excepción al guardar la instalación: ${e.message}")
            }
        }
    }
}
