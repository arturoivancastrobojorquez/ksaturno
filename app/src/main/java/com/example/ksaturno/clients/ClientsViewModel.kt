package com.example.ksaturno.clients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.launch

class ClientsViewModel(private val repository: ClientsRepository) : ViewModel() {

    private val _clients = MutableLiveData<List<Client>>()
    val clients: LiveData<List<Client>> = _clients

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        fetchClients()
    }

    fun fetchClients() {
        viewModelScope.launch {
            try {
                val result = repository.getClients()
                _clients.postValue(result)
            } catch (e: Exception) {
                _toastMessage.postValue(e.message)
            }
        }
    }

    fun createClient(request: CreateClientRequest) {
        viewModelScope.launch {
            val result = repository.createClient(request)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchClients() // Refresh list
            }
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            val result = repository.updateClient(client)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchClients() // Refresh list
            }
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            val result = repository.deleteClient(client.id)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchClients() // Refresh list
            }
        }
    }
}

class ClientsViewModelFactory(private val repository: ClientsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
