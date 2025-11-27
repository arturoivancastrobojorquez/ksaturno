package com.example.ksaturno.facturas

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ksaturno.R
import com.example.ksaturno.databinding.FragmentFacturasFinanzasBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FacturasFinanzasFragment : Fragment() {

    private var _binding: FragmentFacturasFinanzasBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FacturasViewModel
    private lateinit var adapter: FacturasAdapter
    
    private var selectedClientId: Int? = null
    private var selectedEstado: String = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturasFinanzasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FacturasViewModel::class.java)
        
        setupViews()
        setupObservers()
        
        setFragmentResultListener("clientSearchRequest") { _, bundle ->
            val clientId = bundle.getInt("selectedClientId")
            val clientName = bundle.getString("selectedClientName")
            
            selectedClientId = clientId
            binding.tvSelectClient.text = clientName ?: "Cliente seleccionado"
            
            loadData()
            binding.btnGeneratePdf.isEnabled = true
        }
    }

    private fun setupViews() {
        binding.tvSelectClient.setOnClickListener {
             findNavController().navigate(R.id.clientSearchFragment)
        }

        val estados = listOf("Todos", "pendiente", "pagado", "vencido")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFacturaStatus.adapter = spinnerAdapter
        
        binding.spinnerFacturaStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEstado = estados[position]
                loadData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Inicializar adaptador con callback para el botón Pagar
        adapter = FacturasAdapter(
            emptyList(),
            onPayClick = { factura -> showPaymentDialog(factura) }
        )
        binding.rvFacturasCliente.adapter = adapter
        
        binding.btnGeneratePdf.setOnClickListener {
             if (selectedClientId != null) {
                 viewModel.prepareReportData(selectedClientId!!)
             } else {
                 Toast.makeText(context, "Selecciona un cliente primero", Toast.LENGTH_SHORT).show()
             }
        }
    }

    private fun setupObservers() {
        viewModel.facturas.observe(viewLifecycleOwner) { facturas ->
            adapter.updateData(facturas)
            if (facturas.isEmpty() && selectedClientId != null) {
                Toast.makeText(context, "No se encontraron facturas con ese criterio", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnGeneratePdf.isEnabled = !isLoading && selectedClientId != null
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.reportData.observe(viewLifecycleOwner) { adeudos ->
            if (adeudos != null && adeudos.isNotEmpty()) {
                generatePdf(adeudos)
            } else if (adeudos != null) {
                Toast.makeText(context, "Este cliente no tiene facturas pendientes o vencidas", Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.paymentResult.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                loadData() // Refresh list
                viewModel.clearPaymentResult()
            }
        }
    }
    
    private fun showPaymentDialog(factura: Factura) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_registrar_pago, null)
        val etMonto = dialogView.findViewById<EditText>(R.id.et_monto_pago)
        val spinnerMetodo = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_metodo_pago)
        val etComentarios = dialogView.findViewById<EditText>(R.id.et_comentarios_pago)

        // Pre-fill amount with total due
        etMonto.setText(factura.monto.toString())

        // Setup Spinner
        val metodos = listOf("Efectivo", "Transferencia", "Depósito", "Cheque")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, metodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodo.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Registrar Pago - Factura #${factura.numeroFactura}")
            .setView(dialogView)
            .setPositiveButton("Registrar") { _, _ ->
                val monto = etMonto.text.toString().toDoubleOrNull()
                val metodo = spinnerMetodo.selectedItem.toString()
                val comentarios = etComentarios.text.toString()

                if (monto != null && monto > 0) {
                    if (selectedClientId != null) {
                        viewModel.registerPayment(factura.idFactura, selectedClientId!!, monto, metodo, comentarios)
                    }
                } else {
                    Toast.makeText(context, "Monto inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun loadData() {
        if (selectedClientId != null) {
            viewModel.loadFacturas(selectedClientId!!, selectedEstado)
        }
    }
    
    private fun generatePdf(facturas: List<Factura>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        val titlePaint = Paint()

        titlePaint.textSize = 18f
        titlePaint.isFakeBoldText = true
        titlePaint.textAlign = Paint.Align.CENTER

        paint.textSize = 12f
        paint.color = ContextCompat.getColor(requireContext(), android.R.color.black)

        canvas.drawText("REPORTE DE ADEUDOS", 297f, 50f, titlePaint)
        
        val clientName = binding.tvSelectClient.text.toString()
        paint.isFakeBoldText = true
        canvas.drawText("Cliente: $clientName", 50f, 90f, paint)
        canvas.drawText("Fecha de corte: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}", 50f, 110f, paint)
        
        var yPos = 150f
        paint.isFakeBoldText = true
        canvas.drawText("Factura", 50f, yPos, paint)
        canvas.drawText("Fecha", 150f, yPos, paint)
        canvas.drawText("Concepto", 250f, yPos, paint)
        canvas.drawText("Estado", 450f, yPos, paint)
        canvas.drawText("Monto", 520f, yPos, paint)
        
        canvas.drawLine(50f, yPos + 5f, 545f, yPos + 5f, paint)
        yPos += 25f
        
        paint.isFakeBoldText = false
        var total = 0.0
        
        for (factura in facturas) {
            canvas.drawText(factura.numeroFactura, 50f, yPos, paint)
            canvas.drawText(factura.fechaEmision, 150f, yPos, paint)
            
            val concepto = if ((factura.nombreUnidad?.length ?: 0) > 25) 
                             (factura.nombreUnidad?.substring(0, 22) + "...") 
                           else 
                             (factura.nombreUnidad ?: "-")
            canvas.drawText(concepto, 250f, yPos, paint)
            
            canvas.drawText(factura.estado?.uppercase() ?: "-", 450f, yPos, paint)
            canvas.drawText(String.format("$ %.2f", factura.monto), 520f, yPos, paint)
            
            total += factura.monto
            yPos += 20f
            
            if (yPos > 800) {
                 break 
            }
        }
        
        yPos += 20f
        canvas.drawLine(50f, yPos, 545f, yPos, paint)
        yPos += 20f
        paint.isFakeBoldText = true
        canvas.drawText("TOTAL A PAGAR:", 400f, yPos, paint)
        canvas.drawText(String.format("$ %.2f", total), 520f, yPos, paint)

        document.finishPage(page)

        val fileName = "EstadoCuenta_${System.currentTimeMillis()}.pdf"
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, fileName)

        try {
            document.writeTo(FileOutputStream(file))
            
            AlertDialog.Builder(requireContext())
                .setTitle("Reporte Generado")
                .setMessage("El archivo se guardó correctamente.\n\n¿Deseas abrirlo ahora?")
                .setPositiveButton("Sí, Abrir") { _, _ ->
                    openPdf(file)
                }
                .setNegativeButton("Cerrar", null)
                .show()
                
        } catch (e: IOException) {
            Toast.makeText(context, "Error al guardar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }

    private fun openPdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )
            
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            
            val chooser = Intent.createChooser(intent, "Abrir reporte con")
            startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "No hay aplicación para abrir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
