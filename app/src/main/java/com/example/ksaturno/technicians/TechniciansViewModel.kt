package com.example.ksaturno.technicians

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ksaturno.ApiResponse
import kotlinx.coroutines.launch

/**
 * El ViewModel (Vista-Modelo) es el "gerente" o el cerebro de la lógica de la interfaz de usuario.
 * Su misión es:
 * 1. Sobrevivir a los cambios de configuración (como la rotación de pantalla), manteniendo los datos vivos.
 * 2. Gestionar los datos de la interfaz, obteniéndolos desde el Repository.
 * 3. Exponer estos datos a la Vista (el Fragment) a través de objetos 'LiveData', que son observables.
 * 4. Procesar las acciones del usuario que le comunica el Fragment (crear, actualizar, eliminar).
 * El ViewModel NUNCA tiene una referencia directa a la Vista (Fragment/Activity).
 *
 * @param repository El repositorio que se usará para obtener los datos de los técnicos.
 *                   Se inyecta a través de la TechniciansViewModelFactory.
 */
class TechniciansViewModel(private val repository: TechniciansRepository) : ViewModel() {

    // --- LiveData para la lista de técnicos ---
    // _technicians es privado y mutable: solo el ViewModel puede cambiar su valor.
    private val _technicians = MutableLiveData<List<Technician>>()
    // technicians es público e inmutable (LiveData): el Fragment puede leerlo y suscribirse
    // a sus cambios, pero no puede modificarlo directamente.
    val technicians: LiveData<List<Technician>> = _technicians

    // --- LiveData para mostrar mensajes al usuario ---
    // Funciona igual que el anterior, pero para enviar mensajes (éxito, error) a la UI.
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    /**
     * El bloque 'init' se ejecuta una sola vez, cuando el ViewModel es creado por primera vez.
     * Es el lugar perfecto para cargar los datos iniciales.
     */
    init {
        fetchTechnicians()
    }

    /**
     * Obtiene la lista de técnicos desde el repositorio y la publica en el LiveData.
     * Esta función se ejecuta en una corrutina (viewModelScope.launch) para no bloquear el hilo principal.
     */
    fun fetchTechnicians() {
        viewModelScope.launch {
            try {
                // Llama al repositorio para obtener los datos. Esta es una función 'suspend',
                // por lo que la corrutina se pausará aquí hasta que la llamada de red termine.
                val result = repository.getTechnicians()
                // Publica el resultado en el LiveData. `postValue` es seguro para ser llamado
                // desde un hilo de fondo, que es donde se ejecuta esta corrutina.
                _technicians.postValue(result)
            } catch (e: Exception) {
                // Si el repositorio lanza una excepción (error de red, de parsing, etc.),
                // se captura aquí y se publica el mensaje de error en el toastMessage LiveData.
                _toastMessage.postValue(e.message)
            }
        }
    }

    /**
     * Delega la creación de un nuevo técnico al repositorio.
     * @param request El objeto con los datos del nuevo técnico a crear.
     */
    fun createTechnician(request: CreateTechnicianRequest) {
        viewModelScope.launch {
            val result = repository.createTechnician(request)
            _toastMessage.postValue(result.message)
            // Si la operación fue exitosa, se vuelve a llamar a fetchTechnicians()
            // para obtener la lista actualizada desde el servidor y refrescar la pantalla.
            if (result.success) {
                fetchTechnicians()
            }
        }
    }

    /**
     * Delega la actualización de un técnico existente al repositorio.
     * @param technician El objeto del técnico con los datos ya modificados.
     */
    fun updateTechnician(technician: Technician) {
        viewModelScope.launch {
            val result = repository.updateTechnician(technician)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchTechnicians() // Refresca la lista tras la actualización.
            }
        }
    }

    /**
     * Delega la eliminación de un técnico al repositorio.
     * @param technician El objeto del técnico que se va to eliminar.
     */
    fun deleteTechnician(technician: Technician) {
        viewModelScope.launch {
            val result = repository.deleteTechnician(technician.idTecnico)
            _toastMessage.postValue(result.message)
            if (result.success) {
                fetchTechnicians() // Refresca la lista tras la eliminación.
            }
        }
    }
}

/**
 * La Fábrica (Factory) del ViewModel es una clase auxiliar cuyo único propósito es permitir
 * la creación de una instancia de 'TechniciansViewModel' pasándole dependencias (en este caso,
 * el 'TechniciansRepository') a su constructor. Este es el patrón estándar de Android para
 * la inyección de dependencias en los ViewModels.
 */
class TechniciansViewModelFactory(private val repository: TechniciansRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TechniciansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TechniciansViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
