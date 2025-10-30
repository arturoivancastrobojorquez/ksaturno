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

class TechniciansFragment : Fragment() {

    private lateinit var viewModel: TechniciansViewModel
    private var selectedImageBase64: String? = null
    private var photoImageView: ImageView? = null

    // --- ActivityResultLauncher to pick an image from the gallery ---
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            photoImageView?.setImageBitmap(bitmap)

            // --- Convert bitmap to Base64 string ---
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            selectedImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_technicians, container, false)

        val repository = TechniciansRepository(RetrofitClient.instance)
        val factory = TechniciansViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(TechniciansViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_technicians)
        val adapter = TechniciansAdapter(emptyList(), { onEditTechnician(it) }, { onDeleteTechnician(it) })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.technicians.observe(viewLifecycleOwner) {
            adapter.updateTechnicians(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_technician).setOnClickListener {
            showAddOrEditTechnicianDialog(null)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Técnicos"
    }

    private fun onEditTechnician(technician: Technician) {
        showAddOrEditTechnicianDialog(technician)
    }

    private fun onDeleteTechnician(technician: Technician) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Técnico")
            .setMessage("¿Estás seguro de que deseas eliminar a este técnico?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteTechnician(technician) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditTechnicianDialog(technician: Technician?) {
        val context = requireContext()
        val isEditMode = technician != null

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_technician, null)
        photoImageView = dialogView.findViewById(R.id.image_view_technician_photo)
        val selectPhotoButton: Button = dialogView.findViewById(R.id.button_select_photo)
        val nameEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_name)
        val addressEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_address)
        val phoneEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_phone)
        val emailEditText: EditText = dialogView.findViewById(R.id.edit_text_technician_email)

        selectPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        if (isEditMode) {
            nameEditText.setText(technician?.name)
            addressEditText.setText(technician?.address)
            phoneEditText.setText(technician?.phone)
            emailEditText.setText(technician?.email)

            // --- Decode Base64 and set image ---
            technician?.photo?.let {
                if (it.isNotEmpty()) {
                    try {
                        val imageBytes = Base64.decode(it, Base64.DEFAULT)
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        photoImageView?.setImageBitmap(decodedImage)
                        selectedImageBase64 = it
                    } catch (e: IllegalArgumentException) {
                        // Handle case where string is not valid Base64
                        photoImageView?.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                }
            }
        }

        AlertDialog.Builder(context)
            .setTitle(if (isEditMode) "Editar Técnico" else "Nuevo Técnico")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameEditText.text.toString()
                val address = addressEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val email = emailEditText.text.toString()

                if (isEditMode) {
                    val updatedTechnician = technician!!.copy(
                        name = name,
                        address = address,
                        phone = phone,
                        email = email,
                        photo = selectedImageBase64
                    )
                    viewModel.updateTechnician(updatedTechnician)
                } else {
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
