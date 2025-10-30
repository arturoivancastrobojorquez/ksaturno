package com.example.ksaturno.technicians

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.launch

class TechniciansViewModel(private val repository: TechniciansRepository) : ViewModel() {

    private val _technicians = MutableLiveData<List<Technician>>()
    val technicians: LiveData<List<Technician>> = _technicians

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        fetchTechnicians()
    }

    fun fetchTechnicians() {
        viewModelScope.launch {
            try {
                _technicians.postValue(repository.getTechnicians())
            } catch (e: Exception) {
                _toastMessage.postValue(e.message)
            }
        }
    }

    fun createTechnician(request: CreateTechnicianRequest) {
        viewModelScope.launch {
            val result = repository.createTechnician(request)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchTechnicians()
            }
        }
    }

    fun updateTechnician(technician: Technician) {
        viewModelScope.launch {
            val result = repository.updateTechnician(technician)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchTechnicians()
            }
        }
    }

    fun deleteTechnician(technician: Technician) {
        viewModelScope.launch {
            val result = repository.deleteTechnician(technician.id)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchTechnicians()
            }
        }
    }
}

class TechniciansViewModelFactory(private val repository: TechniciansRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TechniciansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TechniciansViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
