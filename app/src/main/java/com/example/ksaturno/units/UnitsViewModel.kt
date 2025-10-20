package com.example.ksaturno.units

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import com.example.ksaturno.categories.Category
import kotlinx.coroutines.launch

class UnitsViewModel(private val repository: UnitsRepository) : ViewModel() {

    private val _units = MutableLiveData<List<Unit>>()
    val units: LiveData<List<Unit>> = _units

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        fetchUnits()
        fetchCategories()
    }

    fun fetchUnits() {
        viewModelScope.launch {
            try {
                val result = repository.getUnits()
                _units.postValue(result)
            } catch (e: Exception) {
                _toastMessage.postValue(e.message)
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = repository.getCategories()
                _categories.postValue(result)
            } catch (e: Exception) {
                _toastMessage.postValue("Error al cargar categorías: ${e.message}")
            }
        }
    }

    fun createUnit(unitRequest: CreateUnitRequest) {
        viewModelScope.launch {
            val result = repository.createUnit(unitRequest)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchUnits() // Refresh list
            }
        }
    }

    fun updateUnit(unit: Unit) {
        viewModelScope.launch {
            val result = repository.updateUnit(unit)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchUnits() // Refresh list
            }
        }
    }

    fun deleteUnit(unit: Unit) {
        viewModelScope.launch {
            val result = repository.deleteUnit(unit.id)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchUnits() // Refresh list
            }
        }
    }
}

class UnitsViewModelFactory(private val repository: UnitsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UnitsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
