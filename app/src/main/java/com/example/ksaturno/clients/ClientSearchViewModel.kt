package com.example.ksaturno.clients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClientSearchViewModel(private val repository: ClientsRepository) : ViewModel() {

    private val _clients = MutableLiveData<List<Client>>()
    val clients: LiveData<List<Client>> = _clients

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        fetchClients()
    }

    private fun fetchClients() {
        viewModelScope.launch {
            try {
                _clients.postValue(repository.getClients())
            } catch (e: Exception) {
                _toastMessage.postValue(e.message)
            }
        }
    }
}

class ClientSearchViewModelFactory(private val repository: ClientsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
