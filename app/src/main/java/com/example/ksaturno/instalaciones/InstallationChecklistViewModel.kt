package com.example.ksaturno.instalaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.checklist.ChecklistItem
import com.example.ksaturno.checklist.ChecklistRepository
import com.example.ksaturno.checklist.CreateListaVerificacionRequest
import kotlinx.coroutines.launch

class InstallationChecklistViewModel(private val installationId: Int) : ViewModel() {

    private val repository = ChecklistRepository(RetrofitClient.instance)

    private val _masterChecklistItems = MutableLiveData<List<ChecklistItem>>()
    val masterChecklistItems: LiveData<List<ChecklistItem>> = _masterChecklistItems

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        fetchMasterChecklist()
    }

    private fun fetchMasterChecklist() {
        viewModelScope.launch {
            try {
                _masterChecklistItems.value = repository.getMasterChecklistItems()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun saveItemState(itemId: Int, isChecked: Boolean, comments: String?) {
        viewModelScope.launch {
            val request = CreateListaVerificacionRequest(
                idInstalacion = installationId,
                idItem = itemId,
                verificado = isChecked,
                comentarios = comments
            )
            try {
                val response = repository.saveChecklistItemState(request)
                if (!response.success) {
                    _error.value = response.message ?: "Error desconocido al guardar el ítem."
                }
                // Optionally, you can show a success message too.
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

// Factory to pass the installationId to the ViewModel
class InstallationChecklistViewModelFactory(private val installationId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstallationChecklistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstallationChecklistViewModel(installationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
