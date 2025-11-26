package com.example.ksaturno.empresa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import kotlinx.coroutines.launch

class EmpresasViewModel : ViewModel() {

    private val repository = EmpresasRepository(RetrofitClient.instance)

    private val _empresas = MutableLiveData<List<Empresa>>()
    val empresas: LiveData<List<Empresa>> = _empresas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _operationStatus = MutableLiveData<Boolean>()
    val operationStatus: LiveData<Boolean> = _operationStatus

    fun fetchEmpresas() {
        viewModelScope.launch {
            try {
                _empresas.value = repository.getEmpresas()
            } catch (e: Exception) {
                _error.value = "Error al cargar empresas: ${e.message}"
            }
        }
    }

    fun createEmpresa(request: CreateEmpresaRequest) {
        viewModelScope.launch {
            val response = repository.createEmpresa(request)
            if (response.success) {
                fetchEmpresas()
                _operationStatus.value = true
            } else {
                _error.value = response.message ?: "Error desconocido"
                _operationStatus.value = false
            }
        }
    }

    fun updateEmpresa(empresa: Empresa) {
        viewModelScope.launch {
            val response = repository.updateEmpresa(empresa)
            if (response.success) {
                fetchEmpresas()
                _operationStatus.value = true
            } else {
                _error.value = response.message ?: "Error desconocido"
                _operationStatus.value = false
            }
        }
    }

    fun deleteEmpresa(id: Int) {
        viewModelScope.launch {
            val response = repository.deleteEmpresa(id)
            if (response.success) {
                fetchEmpresas()
            } else {
                _error.value = response.message ?: "Error al eliminar"
            }
        }
    }
}
