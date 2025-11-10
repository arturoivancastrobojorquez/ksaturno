package com.example.ksaturno.instalaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.servicios.ServiciosRepository
import com.example.ksaturno.technicians.TechniciansRepository
import com.example.ksaturno.units.UnitsRepository
import kotlinx.coroutines.launch

class InstalacionesViewModel : ViewModel() {

    // Repositories for all the data we need to fetch
    private val instalacionesRepository = InstalacionesRepository(RetrofitClient.instance)
    private val serviciosRepository = ServiciosRepository(RetrofitClient.instance)
    private val unitsRepository = UnitsRepository(RetrofitClient.instance)
    private val techniciansRepository = TechniciansRepository(RetrofitClient.instance)

    // The LiveData now holds the display-ready model
    private val _instalacionesDisplay = MutableLiveData<List<InstalacionDisplay>>()
    val instalacionesDisplay: LiveData<List<InstalacionDisplay>> = _instalacionesDisplay

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        fetchInstalaciones()
    }

    fun fetchInstalaciones() {
        viewModelScope.launch {
            try {
                // 1. Fetch all the required lists in parallel
                val instalaciones = instalacionesRepository.getInstalaciones()
                val servicios = serviciosRepository.getServicios()
                val unidades = unitsRepository.getUnits()
                val tecnicos = techniciansRepository.getTechnicians()

                // 2. Map the raw data into the display-ready model
                val displayList = instalaciones.mapNotNull { instalacion ->
                    val servicio = servicios.find { it.idServicio == instalacion.idServicio }
                    val tecnico = tecnicos.find { it.idTecnico == instalacion.idTecnico }
                    val unidad = servicio?.let { serv -> unidades.find { it.idUnidad == serv.idUnidad } }

                    // Only create a display object if all related data is found
                    if (servicio != null && tecnico != null && unidad != null) {
                        InstalacionDisplay(
                            idInstalacion = instalacion.idInstalacion,
                            fechaInstalacion = instalacion.fechaInstalacion,
                            estado = instalacion.estado,
                            componentes = instalacion.componentesInstalados,
                            comentarios = instalacion.comentarios,
                            nombreServicio = servicio.tipo,
                            nombreUnidad = unidad.nombreUnidad,
                            nombreTecnico = tecnico.nombre
                        )
                    } else {
                        null
                    }
                }

                // 3. Post the final list to the UI
                _instalacionesDisplay.postValue(displayList)

            } catch (e: Exception) {
                _error.postValue("Error al cargar y procesar los datos de las instalaciones: ${e.message}")
            }
        }
    }
}
