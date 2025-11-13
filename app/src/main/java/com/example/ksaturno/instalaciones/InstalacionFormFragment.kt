package com.example.ksaturno.instalaciones

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    }

    private fun setupViews() {
        // Setup Date Picker
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

        // Button now triggers the creation process
        binding.btnNextStep.setOnClickListener { saveAndProceed() }
    }

    private fun setupObservers() {
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

        // Observer for the new installation ID
        viewModel.newInstallationId.observe(viewLifecycleOwner) { newId ->
            if (newId != null) {
                // Navigate to the next step with the new ID
                val action = InstalacionFormFragmentDirections.actionStep1ToStep2(newId)
                findNavController().navigate(action)
                viewModel.onNavigationComplete() // Reset the state
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveAndProceed() {
        val servicio = binding.spinnerServicio.selectedItem as? Servicio
        val tecnico = binding.spinnerTecnico.selectedItem as? Technician

        if (servicio == null || tecnico == null) {
            Toast.makeText(context, "Por favor, selecciona un servicio y un técnico", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateInstalacionRequest(
            idServicio = servicio.idServicio,
            idTecnico = tecnico.idTecnico,
            fechaInstalacion = binding.etFechaInstalacion.text.toString(),
            componentesInstalados = binding.etComponentes.text.toString(),
            estado = binding.etEstado.text.toString(),
            comentarios = binding.etComentarios.text.toString()
        )

        viewModel.createInstalacion(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
