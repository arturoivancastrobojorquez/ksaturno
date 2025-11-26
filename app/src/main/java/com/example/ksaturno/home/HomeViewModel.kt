package com.example.ksaturno.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = HomeRepository()

    private val _renewalAlerts = MutableLiveData<List<RenewalAlert>>()
    val renewalAlerts: LiveData<List<RenewalAlert>> = _renewalAlerts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _totalRenewals = MutableLiveData<Int>()
    val totalRenewals: LiveData<Int> = _totalRenewals

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val alerts = repository.getRenewalAlerts()
                _renewalAlerts.value = alerts
                _totalRenewals.value = alerts.size
                _totalAmount.value = alerts.sumOf { it.monto }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
