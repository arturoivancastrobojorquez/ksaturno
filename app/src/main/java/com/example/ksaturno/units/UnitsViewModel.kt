package com.example.ksaturno.units

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.categories.CategoriesRepository
import com.example.ksaturno.categories.Category
import com.example.ksaturno.clients.Client
import com.example.ksaturno.clients.ClientsRepository
import kotlinx.coroutines.launch

class UnitsViewModel : ViewModel() {

    private val repository = UnitsRepository(RetrofitClient.instance)
    private val clientsRepository = ClientsRepository(RetrofitClient.instance)
    private val categoriesRepository = CategoriesRepository(RetrofitClient.instance)

    private val _units = MutableLiveData<List<Unit>>()
    val units: LiveData<List<Unit>> get() = _units

    private val _allClients = MutableLiveData<List<Client>>()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _selectedClient = MutableLiveData<Client?>()
    val selectedClient: LiveData<Client?> get() = _selectedClient

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    init {
        fetchUnits()
        fetchAllClients()
        fetchCategories()
    }

    fun fetchUnits() {
        viewModelScope.launch {
            try {
                _units.value = repository.getUnits()
            } catch (e: Exception) {
                handleError("Error al cargar unidades", e)
            }
        }
    }

    private fun fetchAllClients() {
        viewModelScope.launch {
            try {
                _allClients.value = clientsRepository.getClients()
            } catch (e: Exception) {
                handleError("Error al cargar clientes", e)
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoriesRepository.getCategories()
            } catch (e: Exception) {
                handleError("Error al cargar categorías", e)
            }
        }
    }

    fun onClientSelected(clientId: Int) {
        _selectedClient.value = _allClients.value?.find { it.id == clientId }
    }

    fun prepareForEdit(unit: Unit) {
        _selectedClient.value = _allClients.value?.find { it.id == unit.idCliente }
    }

    fun clearSelection() {
        _selectedClient.value = null
    }

    fun createUnit(request: CreateUnitRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createUnit(request)
                if (response.success) {
                    fetchUnits() 
                    _successMessage.value = response.message
                } else {
                    _errorMessage.value = "Error al crear: ${response.message}"
                }
            } catch (e: Exception) {
                handleError("Excepción al crear unidad", e)
            }
        }
    }

    fun updateUnit(unit: Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateUnit(unit)
                if (response.success) {
                    fetchUnits()
                    _successMessage.value = response.message
                } else {
                    _errorMessage.value = "Error al actualizar: ${response.message}"
                }
            } catch (e: Exception) {
                handleError("Excepción al actualizar unidad", e)
            }
        }
    }

    fun deleteUnit(unitId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUnit(unitId)
                if (response.success) {
                    fetchUnits()
                    _successMessage.value = response.message
                } else {
                    _errorMessage.value = "Error al eliminar: ${response.message}"
                }
            } catch (e: Exception) {
                handleError("Excepción al eliminar unidad", e)
            }
        }
    }
    
    // Public function to post validation errors from the Fragment
    fun postValidationError(message: String) {
        _errorMessage.value = message
    }

    private fun handleError(message: String, e: Exception) {
        val errorMsg = "$message: ${e.message}"
        _errorMessage.value = message
        Log.e("UnitsViewModel", errorMsg, e)
    }
}
