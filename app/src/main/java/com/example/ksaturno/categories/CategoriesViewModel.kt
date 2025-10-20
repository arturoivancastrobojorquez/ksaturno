package com.example.ksaturno.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoriesRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = repository.getCategories()
                _categories.postValue(result)
            } catch (e: Exception) {
                _toastMessage.postValue(e.message)
            }
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            val result = repository.createCategory(name)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchCategories() // Refresh list
            }
        }
    }

    fun updateCategory(id: Int, name: String) {
        viewModelScope.launch {
            val result = repository.updateCategory(id, name)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchCategories() // Refresh list
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            val result = repository.deleteCategory(category.id)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchCategories() // Refresh list
            }
        }
    }
}

class CategoriesViewModelFactory(private val repository: CategoriesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
