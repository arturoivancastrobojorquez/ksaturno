package com.example.ksaturno.instalaciones

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.checklist.CompletedChecklistItem
import com.example.ksaturno.evidencias.CreateEvidenciaRequest
import com.example.ksaturno.evidencias.EvidenceRepository
import kotlinx.coroutines.launch

class InstallationEvidenceViewModel(private val installationId: Int) : ViewModel() {

    private val repository = EvidenceRepository(RetrofitClient.instance)

    private val _evidencePendingItems = MutableLiveData<List<CompletedChecklistItem>>()
    val evidencePendingItems: LiveData<List<CompletedChecklistItem>> = _evidencePendingItems

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    init {
        fetchEvidencePendingItems()
    }

    private fun fetchEvidencePendingItems() {
        viewModelScope.launch {
            try {
                _evidencePendingItems.value = repository.getEvidencePendingItems(installationId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun saveEvidence(idListaVerificacion: Int, imageUri: Uri, description: String?) {
        viewModelScope.launch {
            // In a real app, you would upload the image file to a server and get back a URL.
            // For this example, we'll just save the local Uri path as a string.
            val imagePath = imageUri.toString()

            val request = CreateEvidenciaRequest(
                idListaVerificacion = idListaVerificacion,
                rutaArchivo = imagePath,
                descripcion = description
            )

            try {
                val response = repository.saveEvidence(request)
                if (response.success) {
                    _saveSuccess.value = true
                    // Optionally, you could remove the item from the list after saving.
                } else {
                    _error.value = response.message ?: "Error desconocido al guardar la evidencia."
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun onSaveHandled() {
        _saveSuccess.value = false
    }
}

class InstallationEvidenceViewModelFactory(private val installationId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstallationEvidenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstallationEvidenceViewModel(installationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
