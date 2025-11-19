package com.example.ksaturno.instalaciones

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ksaturno.R
import com.example.ksaturno.databinding.FragmentInstalacionFormBinding
import com.example.ksaturno.servicios.Servicio
import com.example.ksaturno.technicians.Technician
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InstalacionFormFragment : Fragment() {

    private var _binding: FragmentInstalacionFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InstalacionFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstalacionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(InstalacionFormViewModel::class.java)

        setupViews()
        setupObservers()

        viewModel.fetchSpinnerData()
        
        // Listener for client search result
        setFragmentResultListener("clientSearchRequest") { _, bundle ->
            val clientId = bundle.getInt("selectedClientId")
            val clientName = bundle.getString("selectedClientName")
            viewModel.processClientSearchResult(clientId, clientName)
        }
    }

    private fun setupViews() {
        // Client Selection
        binding.textViewSelectClient.setOnClickListener {
            findNavController().navigate(R.id.clientSearchFragment)
        }

        // Date Picker
        val calendar = Calendar.getInstance()
        binding.etFechaInstalacion.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    binding.etFechaInstalacion.setText(format.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Status Spinner
        val statusOptions = listOf("fallida", "completada", "en_progreso")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = statusAdapter

        binding.btnNextStep.setOnClickListener { saveAndProceed() }
    }

    private fun setupObservers() {
        viewModel.selectedClient.observe(viewLifecycleOwner) { client ->
            binding.textViewSelectClient.text = client?.nombre ?: "Seleccionar cliente..."
        }

        viewModel.servicios.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerServicio.adapter = adapter
        }

        viewModel.tecnicos.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerTecnico.adapter = adapter
        }

        viewModel.newInstallationId.observe(viewLifecycleOwner) { newId ->
            if (newId != null) {
                val action = InstalacionFormFragmentDirections.actionStep1ToStep2(newId)
                findNavController().navigate(action)
                viewModel.onNavigationComplete()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun saveAndProceed() {
        val servicio = binding.spinnerServicio.selectedItem as? Servicio
        val tecnico = binding.spinnerTecnico.selectedItem as? Technician
        val estado = binding.spinnerEstado.selectedItem as? String

        if (viewModel.selectedClient.value == null) {
            Toast.makeText(context, "Por favor, selecciona un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        if (servicio == null || tecnico == null || estado == null) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateInstalacionRequest(
            id_servicio = servicio.idServicio,
            id_tecnico = tecnico.idTecnico,
            fecha_instalacion = binding.etFechaInstalacion.text.toString(),
            componentes_instalados = binding.etComponentes.text.toString(),
            estado = estado,
            comentarios = binding.etComentarios.text.toString()
        )

        viewModel.createInstalacion(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
