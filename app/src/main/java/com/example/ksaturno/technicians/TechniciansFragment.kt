package com.example.ksaturno.technicians

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

/**
 * El Fragmento (Fragment) es el controlador principal de la interfaz de usuario para la sección de Técnicos.
 * Actúa como la "Vista" en la arquitectura MVVM (Model-View-ViewModel). Sus responsabilidades son:
 * 1. Mostrar la lista de técnicos.
 * 2. Escuchar las interacciones del usuario (clics en botones de agregar, editar, eliminar).
 * 3. Comunicar estas acciones al `TechniciansViewModel` para que se ejecute la lógica de negocio.
 * 4. Observar los cambios de datos en el `ViewModel` y actualizar la UI en consecuencia.
 */
class TechniciansFragment : Fragment() {

    // Referencia al ViewModel, que sobrevive a los cambios de configuración y gestiona los datos.
    private lateinit var viewModel: TechniciansViewModel
    // Variable para almacenar temporalmente la imagen seleccionada, codificada en Base64.
    private var selectedImageBase64: String? = null
    // Referencia al ImageView del diálogo para poder actualizar la foto desde el callback del selector de imágenes.
    private var photoImageView: ImageView? = null

    /**
     * Es el mecanismo moderno de Android para manejar los resultados de actividades, como la galería de imágenes.
     * Reemplaza al antiguo `onActivityResult`.
     * Cuando el usuario selecciona una imagen (devuelve una `Uri`), este bloque de código se ejecuta.
     */
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { // Si el usuario realmente seleccionó una imagen (y no canceló)
            // Abre un flujo de datos desde la Uri de la imagen.
            val inputStream = requireContext().contentResolver.openInputStream(it)
            // Decodifica el flujo de datos en un objeto Bitmap (una imagen en memoria).
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Muestra la imagen seleccionada en el ImageView del diálogo.
            photoImageView?.setImageBitmap(bitmap)

            // --- Proceso de conversión de la imagen a una cadena de texto Base64 ---
            val byteArrayOutputStream = ByteArrayOutputStream()
            // Comprime el bitmap a formato JPEG con una calidad del 50% para reducir su tamaño.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            // Convierte la imagen comprimida a un array de bytes.
            val byteArray = byteArrayOutputStream.toByteArray()
            // Codifica el array de bytes a una cadena Base64 y la guarda en la variable.
            selectedImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    /**
     * Método del ciclo de vida del Fragmento donde se crea la jerarquía de vistas.
     * Aquí se "infla" el layout XML y se configuran las vistas y los observadores.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Carga el layout 'fragment_technicians.xml' en un objeto View.
        val view = inflater.inflate(R.layout.fragment_technicians, container, false)

        // --- Configuración de la arquitectura MVVM ---
        // Se crea el Repositorio (el especialista en datos) pasándole la instancia de Retrofit.
        val repository = TechniciansRepository(RetrofitClient.instance)
        // Se crea la Fábrica del ViewModel, inyectándole el repositorio.
        val factory = TechniciansViewModelFactory(repository)
        // Se obtiene la instancia del ViewModel. ViewModelProvider se asegura de que sea la misma
        // instancia incluso si la pantalla rota, manteniendo los datos.
        viewModel = ViewModelProvider(this, factory).get(TechniciansViewModel::class.java)

        // --- Configuración del RecyclerView (la lista visual) ---
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_technicians)
        // Se crea el Adaptador, pasándole las funciones que debe llamar cuando se pulse editar o eliminar.
        val adapter = TechniciansAdapter(emptyList(), { onEditTechnician(it) }, { onDeleteTechnician(it) })
        // Se le dice al RecyclerView que organice sus elementos en una lista vertical.
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Se conecta el Adaptador con el RecyclerView.
        recyclerView.adapter = adapter

        // --- Observadores de LiveData (el corazón de la reactividad) ---
        // El Fragmento se suscribe a los cambios en la lista de técnicos del ViewModel.
        viewModel.technicians.observe(viewLifecycleOwner) {
            // Cuando la lista de técnicos en el ViewModel cambia, este bloque se ejecuta
            // y le pasa la nueva lista al adaptador para que actualice la pantalla.
            adapter.updateTechnicians(it)
        }

        // Se suscribe a los mensajes (Toast) que el ViewModel quiera mostrar.
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // --- Configuración de Eventos de Usuario ---
        // Se asigna un listener al botón flotante de "agregar".
        view.findViewById<FloatingActionButton>(R.id.fab_add_technician).setOnClickListener {
            // Llama a la función para mostrar el diálogo en modo "creación" (pasando null).
            showAddOrEditTechnicianDialog(null)
        }

        return view
    }

    /**
     * Este método se llama justo después de que la vista del fragmento ha sido creada.
     * Es el lugar ideal para interactuar con la jerarquía de vistas, como en este caso,
     * para cambiar el título de la Toolbar de la actividad principal.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Técnicos"
    }

    /**
     * Esta función es llamada por el Adaptador cuando el usuario pulsa el ícono de editar.
     * @param technician El objeto del técnico que se quiere editar.
     */
    private fun onEditTechnician(technician: Technician) {
        // Llama a la función del diálogo en modo "edición", pasando el objeto técnico.
        showAddOrEditTechnicianDialog(technician)
    }

    /**
     * Esta función es llamada por el Adaptador cuando el usuario pulsa el ícono de eliminar.
     * @param technician El objeto del técnico que se quiere eliminar.
     */
    private fun onDeleteTechnician(technician: Technician) {
        // Muestra un diálogo de confirmación antes de realizar la acción.
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Técnico")
            .setMessage("¿Estás seguro de que deseas eliminar a este técnico?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteTechnician(technician) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Muestra el diálogo para agregar un nuevo técnico o editar uno existente.
     * Esta función es el centro de la lógica del formulario.
     * @param technician Si es 'null', el diálogo se abre en modo "Crear". Si se pasa un objeto,
     *                   se abre en modo "Editar" y rellena los campos con sus datos.
     */
    private fun showAddOrEditTechnicianDialog(technician: Technician?) {
        val context = requireContext()
        val isEditMode = technician != null

        // Infla el layout personalizado 'dialog_add_technician.xml' para usarlo en el diálogo.
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_technician, null)
        photoImageView = dialogView.findViewById(R.id.image_view_technician_photo)
        val selectPhotoButton: Button = dialogView.findViewById(R.id.button_select_photo)
        val nameEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_name)
        val addressEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_address)
        val phoneEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_phone)
        val emailEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_email)

        // Asigna el listener al botón para lanzar el selector de imágenes.
        selectPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Si estamos en modo edición, rellena los campos con los datos del técnico.
        if (isEditMode) {
            nameEditText.setText(technician?.name)
            addressEditText.setText(technician?.address)
            phoneEditText.setText(technician?.phone)
            emailEditText.setText(technician?.email)

            // --- Lógica para decodificar y mostrar la imagen existente ---
            technician?.photo?.let {
                if (it.isNotEmpty()) {
                    try {
                        // Intenta decodificar la cadena Base64 a un array de bytes.
                        val imageBytes = Base64.decode(it, Base64.DEFAULT)
                        // Convierte el array de bytes a un Bitmap y lo muestra en el ImageView.
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        photoImageView?.setImageBitmap(decodedImage)
                        // Guarda la cadena Base64 por si el usuario no la cambia.
                        selectedImageBase64 = it
                    } catch (e: IllegalArgumentException) {
                        // Si la cadena en la BD no es un Base64 válido, muestra una imagen por defecto.
                        photoImageView?.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                }
            }
        }

        // Construye y muestra el diálogo de alerta.
        AlertDialog.Builder(context)
            .setTitle(if (isEditMode) "Editar Técnico" else "Nuevo Técnico")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                // Recoge los datos de los campos del formulario.
                val name = nameEditText.text.toString()
                val address = addressEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()

                // Decide si debe llamar a la función de actualizar o de crear del ViewModel.
                if (isEditMode) {
                    // Crea una copia del técnico original con los datos modificados.
                    val updatedTechnician = technician!!.copy(
                        name = name,
                        address = address,
                        phone = phone,
                        email = email,
                        photo = selectedImageBase64 // Usa la nueva imagen o la original si no se cambió.
                    )
                    viewModel.updateTechnician(updatedTechnician)
                } else {
                    // Crea una nueva petición de creación.
                    val request = CreateTechnicianRequest(
                        name = name,
                        address = address,
                        phone = phone,
                        email = email,
                        photo = selectedImageBase64
                    )
                    viewModel.createTechnician(request)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
