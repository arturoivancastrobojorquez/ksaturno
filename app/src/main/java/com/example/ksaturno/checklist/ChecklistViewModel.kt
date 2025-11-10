package com.example.ksaturno.checklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.launch

/**
 * VIEWMODEL: El cerebro de la UI para la Lista de Verificación.
 * Gestiona los datos y la lógica de negocio, comunicándose con el Repository.
 */
class ChecklistViewModel(private val repository: ChecklistRepository) : ViewModel() {

    // LiveData privado y mutable para la lista de ítems. Solo el ViewModel lo modifica.
    private val _checklistItems = MutableLiveData<List<ChecklistItem>>()
    // LiveData público e inmutable. El Fragment lo observa para recibir actualizaciones.
    val checklistItems: LiveData<List<ChecklistItem>> = _checklistItems

    // LiveData para enviar mensajes (como Toasts) a la UI.
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // El bloque init se ejecuta al crear el ViewModel. Carga los datos iniciales.
    init {
        fetchChecklistItems()
    }

    fun fetchChecklistItems() {
        viewModelScope.launch {
            try {
                val result = repository.getChecklistItems()
                _checklistItems.postValue(result)
            } catch (e: Exception) {
                _toastMessage.postValue(e.message)
            }
        }
    }

    fun createChecklistItem(request: CreateChecklistItemRequest) {
        viewModelScope.launch {
            val result = repository.createChecklistItem(request)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchChecklistItems() // Refresca la lista si la creación fue exitosa.
            }
        }
    }

    fun updateChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            val result = repository.updateChecklistItem(item)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchChecklistItems() // Refresca la lista.
            }
        }
    }

    fun deleteChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            val result = repository.deleteChecklistItem(item.id)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchChecklistItems() // Refresca la lista.
            }
        }
    }
}

/**
 * FACTORY: Clase auxiliar necesaria para poder crear un ChecklistViewModel
 * pasándole el ChecklistRepository en su constructor.
 */
class ChecklistViewModelFactory(private val repository: ChecklistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChecklistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChecklistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
