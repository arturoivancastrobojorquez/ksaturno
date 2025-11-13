package com.example.ksaturno.servicios

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.units.Unit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

class ServiciosFragment : Fragment() {

    private lateinit var viewModel: ServiciosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_servicios, container, false)
        viewModel = ViewModelProvider(this).get(ServiciosViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_servicios)
        val adapter = ServiciosAdapter(emptyList(), { onEditServicio(it) }, { onDeleteServicio(it) })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.servicios.observe(viewLifecycleOwner) {
            adapter.updateServicios(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_servicio).setOnClickListener {
            viewModel.clearSelection()
            showAddOrEditServicioDialog(null)
        }

        setFragmentResultListener("clientSearchRequest") { _, bundle ->
            val clientId = bundle.getInt("selectedClientId")
            viewModel.processClientSearchResult(clientId)
        }

        return view
    }

    private fun onEditServicio(servicio: Servicio) {
        viewModel.prepareForEdit(servicio)
        showAddOrEditServicioDialog(servicio)
    }

    private fun onDeleteServicio(servicio: Servicio) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Servicio")
            .setMessage("¿Estás seguro de que deseas eliminar este servicio?")
            .setPositiveButton("Sí") { _, _ -> viewModel.deleteServicio(servicio) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showAddOrEditServicioDialog(servicio: Servicio?) {
        val isEditMode = servicio != null
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_servicio, null)
        var dialog: AlertDialog? = null

        val selectClientTextView: TextView = dialogView.findViewById(R.id.text_view_select_client_for_service)
        val unitSpinner: Spinner = dialogView.findViewById(R.id.spinner_unit_for_service)
        val typeSpinner: Spinner = dialogView.findViewById(R.id.spinner_service_type)
        val statusSpinner: Spinner = dialogView.findViewById(R.id.spinner_service_status)
        val paymentPeriodSpinner: Spinner = dialogView.findViewById(R.id.spinner_payment_period)
        val startDateEditText: EditText = dialogView.findViewById(R.id.edit_text_service_start_date)
        val endDateEditText: EditText = dialogView.findViewById(R.id.edit_text_service_end_date)
        val dueDateEditText: EditText = dialogView.findViewById(R.id.edit_text_service_due_date)
        val amountEditText: EditText = dialogView.findViewById(R.id.edit_text_service_amount)
        val simCardEditText: EditText = dialogView.findViewById(R.id.edit_text_sim_card)
        val iccidEditText: EditText = dialogView.findViewById(R.id.edit_text_iccid)
        val numPeriodsEditText: EditText = dialogView.findViewById(R.id.edit_text_num_periods)
        val commentsEditText: EditText = dialogView.findViewById(R.id.edit_text_service_comments)

        val serviceTypes = listOf("renovacion", "instalacion", "mantenimiento", "otro")
        val serviceStatus = listOf("vencido", "pendiente", "pagado")
        val paymentPeriods = listOf("anual", "semestral", "bimestral", "mensual")

        typeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceTypes).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        statusSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceStatus).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        paymentPeriodSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentPeriods).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf<Unit>()).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        unitSpinner.adapter = unitAdapter

        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currencyFormat = NumberFormat.getCurrencyInstance()
        val startDateCalendar = Calendar.getInstance()
        val endDateCalendar = Calendar.getInstance()
        val dueDateCalendar = Calendar.getInstance()

        fun updateNumberOfPeriods() {
            if (startDateEditText.text.isNotBlank() && endDateEditText.text.isNotBlank()) {
                numPeriodsEditText.setText(viewModel.calculateNumberOfPeriods(startDateCalendar.time, endDateCalendar.time, paymentPeriodSpinner.selectedItem.toString()).toString())
            }
        }

        fun setupDatePicker(editText: EditText, calendar: Calendar) {
            editText.setOnClickListener {
                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    editText.setText(displayFormat.format(calendar.time))
                    updateNumberOfPeriods()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        setupDatePicker(startDateEditText, startDateCalendar)
        setupDatePicker(endDateEditText, endDateCalendar)
        setupDatePicker(dueDateEditText, dueDateCalendar)

        paymentPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { updateNumberOfPeriods() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        selectClientTextView.setOnClickListener { findNavController().navigate(R.id.clientSearchFragment) }

        viewModel.selectedClient.observe(viewLifecycleOwner) { client ->
            selectClientTextView.text = client?.nombre ?: "Seleccionar cliente..."
        }

        viewModel.filteredUnits.observe(viewLifecycleOwner) {
            unitAdapter.clear(); unitAdapter.addAll(it); unitAdapter.notifyDataSetChanged()
            if (isEditMode) {
                val unitPos = it.indexOfFirst { unit -> unit.idUnidad == servicio?.idUnidad }; if (unitPos >= 0) unitSpinner.setSelection(unitPos)
            }
        }
        
        fun parseDate(dateString: String?): Date? = if (dateString.isNullOrEmpty()) null else try { apiFormat.parse(dateString) } catch (e: ParseException) { null }

        if (isEditMode) {
            typeSpinner.setSelection(serviceTypes.indexOf(servicio?.tipo).coerceAtLeast(0))
            statusSpinner.setSelection(serviceStatus.indexOf(servicio?.estado).coerceAtLeast(0))
            paymentPeriodSpinner.setSelection(paymentPeriods.indexOf(servicio?.periodoPago).coerceAtLeast(0))
            parseDate(servicio?.fechaInicio)?.let { startDateCalendar.time = it; startDateEditText.setText(displayFormat.format(it)) }
            parseDate(servicio?.fechaFin)?.let { endDateCalendar.time = it; endDateEditText.setText(displayFormat.format(it)) }
            parseDate(servicio?.fechaVencimiento)?.let { dueDateCalendar.time = it; dueDateEditText.setText(displayFormat.format(it)) }
            amountEditText.setText(currencyFormat.format(servicio?.monto ?: 0.0))
            simCardEditText.setText(servicio?.tarjetaSim)
            iccidEditText.setText(servicio?.iccid)
            numPeriodsEditText.setText(servicio?.numPeriodos?.toString() ?: "")
            commentsEditText.setText(servicio?.comentarios)
        }

        dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (isEditMode) "Editar Servicio" else "Agregar Servicio")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()

        val saveSuccessObserver = { success: Boolean -> if (success) { dialog?.dismiss(); viewModel.onSaveOperationComplete() } }
        viewModel.saveOperationSuccessful.observe(viewLifecycleOwner, saveSuccessObserver)
        dialog.setOnDismissListener { viewModel.saveOperationSuccessful.removeObserver(saveSuccessObserver); viewModel.clearSelection() }

        dialog.setOnShowListener { 
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener { 
                if (viewModel.selectedClient.value == null && !isEditMode) { Toast.makeText(context, "Por favor, selecciona un cliente", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

                val selectedUnit = unitSpinner.selectedItem as? Unit
                if (selectedUnit == null) { Toast.makeText(context, "Por favor, selecciona una unidad", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                if (startDateEditText.text.isBlank()) { Toast.makeText(context, "Por favor, selecciona una fecha de inicio", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                val amountText = amountEditText.text.toString().replace(Regex("[$,]"), ""); val amount = amountText.toDoubleOrNull()
                if (amount == null || amount <= 0.0) { Toast.makeText(context, "Por favor, ingresa un monto válido", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

                val selectedType = typeSpinner.selectedItem as String
                val selectedStatus = statusSpinner.selectedItem as String
                val selectedPaymentPeriod = paymentPeriodSpinner.selectedItem as String
                val numPeriods = numPeriodsEditText.text.toString().toIntOrNull()
                val simCard = simCardEditText.text.toString()
                val iccid = iccidEditText.text.toString()
                val comments = commentsEditText.text.toString()

                if (isEditMode) {
                    val updatedServicio = servicio!!.copy(
                        idUnidad = selectedUnit.idUnidad, tipo = selectedType, fechaInicio = apiFormat.format(startDateCalendar.time),
                        fechaFin = if(endDateEditText.text.isNotBlank()) apiFormat.format(endDateCalendar.time) else null, fechaVencimiento = if(dueDateEditText.text.isNotBlank()) apiFormat.format(dueDateCalendar.time) else null,
                        monto = amount, estado = selectedStatus, numPeriodos = numPeriods, 
                        comentarios = comments, periodoPago = selectedPaymentPeriod, 
                        tarjetaSim = simCard.ifBlank { null }, iccid = iccid.ifBlank { null })
                    viewModel.updateServicio(updatedServicio)
                } else {
                    val newServicio = CreateServicioRequest(
                        idUnidad = selectedUnit.idUnidad, tipo = selectedType, fechaInicio = apiFormat.format(startDateCalendar.time),
                        fechaFin = if(endDateEditText.text.isNotBlank()) apiFormat.format(endDateCalendar.time) else null, fechaVencimiento = if(dueDateEditText.text.isNotBlank()) apiFormat.format(dueDateCalendar.time) else null,
                        monto = amount, estado = selectedStatus, numPeriodos = numPeriods, 
                        comentarios = comments, idFactura = null, periodoPago = selectedPaymentPeriod, 
                        tarjetaSim = simCard.ifBlank { null }, iccid = iccid.ifBlank { null })
                    viewModel.createServicio(newServicio)
                }
            }
        }
        dialog.show()
    }
}
