package com.example.ksaturno.reports

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ksaturno.R
import com.example.ksaturno.databinding.FragmentReportsBinding
import com.example.ksaturno.empresa.Empresa
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.cardReportPagos.setOnClickListener { viewModel.generateReport(ReportType.PAGOS) }
        binding.cardReportRecuperar.setOnClickListener { viewModel.generateReport(ReportType.LINEAS_RECUPERAR) }
        binding.cardReportSuspendidas.setOnClickListener { viewModel.generateReport(ReportType.LINEAS_SUSPENDIDAS) }
        binding.cardReportVencidas.setOnClickListener { viewModel.generateReport(ReportType.RENOVACIONES_VENCIDAS) }
        binding.cardReportFacturas.setOnClickListener { viewModel.generateReport(ReportType.FACTURAS_SERVICIOS) }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }

        // Observers for each report type
        viewModel.reportePagos.observe(viewLifecycleOwner) { data ->
            if (!data.isNullOrEmpty()) generatePdfPagos(data)
            else showEmptyMessage()
        }
        viewModel.reporteLineasRecuperar.observe(viewLifecycleOwner) { data ->
            if (!data.isNullOrEmpty()) generatePdfLineasRecuperar(data)
            else showEmptyMessage()
        }
        viewModel.reporteLineasSuspendidas.observe(viewLifecycleOwner) { data ->
            if (!data.isNullOrEmpty()) generatePdfLineasSuspendidas(data)
            else showEmptyMessage()
        }
        viewModel.reporteRenovacionesVencidas.observe(viewLifecycleOwner) { data ->
            if (!data.isNullOrEmpty()) generatePdfRenovacionesVencidas(data)
            else showEmptyMessage()
        }
        viewModel.reporteFacturasServicios.observe(viewLifecycleOwner) { data ->
            if (!data.isNullOrEmpty()) generatePdfFacturasServicios(data)
            else showEmptyMessage()
        }
    }

    private fun showEmptyMessage() {
        Toast.makeText(context, "No hay datos para generar este reporte", Toast.LENGTH_SHORT).show()
    }

    // --- PDF GENERATION LOGIC ---

    private fun createPdfDocument(): PdfDocument {
        return PdfDocument()
    }

    private fun startPage(document: PdfDocument): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        return document.startPage(pageInfo)
    }

    private fun drawHeader(canvas: Canvas, title: String, empresa: Empresa?) {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 12f
        
        // Empresa Info (Left)
        var yPos = 40f
        if (empresa != null) {
            paint.isFakeBoldText = true
            canvas.drawText(empresa.nombre, 40f, yPos, paint)
            yPos += 15f
            paint.isFakeBoldText = false
            paint.textSize = 10f
            canvas.drawText(empresa.direccion, 40f, yPos, paint)
            yPos += 15f
            canvas.drawText("Tel: ${empresa.telefono} | RFC: ${empresa.rfc}", 40f, yPos, paint)
        } else {
            canvas.drawText("Empresa no configurada", 40f, yPos, paint)
        }

        // Report Title (Center/Right)
        val titlePaint = Paint()
        titlePaint.textSize = 18f
        titlePaint.isFakeBoldText = true
        titlePaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(title, 555f, 50f, titlePaint)
        
        val datePaint = Paint()
        datePaint.textSize = 10f
        datePaint.textAlign = Paint.Align.RIGHT
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generado: $date", 555f, 70f, datePaint)

        // Separator
        val linePaint = Paint()
        linePaint.strokeWidth = 1f
        canvas.drawLine(40f, 90f, 555f, 90f, linePaint)
    }

    private fun saveAndOpenPdf(document: PdfDocument, fileName: String) {
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, fileName)

        try {
            document.writeTo(FileOutputStream(file))
            document.close()

            AlertDialog.Builder(requireContext())
                .setTitle("Reporte Generado")
                .setMessage("Guardado en: ${file.name}\n¿Deseas abrirlo?")
                .setPositiveButton("Sí") { _, _ -> openPdf(file) }
                .setNegativeButton("Cerrar", null)
                .show()
        } catch (e: IOException) {
            Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
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
            startActivity(Intent.createChooser(intent, "Abrir reporte"))
        } catch (e: Exception) {
            Toast.makeText(context, "No hay app para ver PDF", Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Detalle de Pagos
    private fun generatePdfPagos(data: List<ReportePago>) {
        val doc = createPdfDocument()
        val page = startPage(doc)
        val canvas = page.canvas
        val paint = Paint()
        
        drawHeader(canvas, "DETALLE DE PAGOS", viewModel.empresaInfo.value)

        var y = 120f
        // Table Header
        paint.isFakeBoldText = true
        paint.textSize = 10f
        canvas.drawText("Fecha", 40f, y, paint)
        canvas.drawText("Cliente", 100f, y, paint)
        canvas.drawText("Tipo", 250f, y, paint)
        canvas.drawText("Servicios", 320f, y, paint)
        canvas.drawText("Monto", 500f, y, paint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f

        paint.isFakeBoldText = false
        var total = 0.0

        for (item in data) {
            canvas.drawText(item.fechaPago, 40f, y, paint)
            
            val cliente = if (item.cliente.length > 20) item.cliente.substring(0, 18) + "..." else item.cliente
            canvas.drawText(cliente, 100f, y, paint)
            
            canvas.drawText(item.tipoPago ?: "-", 250f, y, paint)
            
            val serv = if ((item.serviciosCubiertos?.length ?: 0) > 25) item.serviciosCubiertos!!.substring(0, 22) + "..." else item.serviciosCubiertos ?: "-"
            canvas.drawText(serv, 320f, y, paint)
            
            canvas.drawText(String.format("$ %.2f", item.monto), 500f, y, paint)
            
            total += item.monto
            y += 15f
            if (y > 800) break // Simple pagination break
        }
        
        y += 10f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f
        paint.isFakeBoldText = true
        canvas.drawText("TOTAL PAGOS: $ ${String.format("%.2f", total)}", 400f, y, paint)

        doc.finishPage(page)
        saveAndOpenPdf(doc, "Reporte_Pagos_${System.currentTimeMillis()}.pdf")
    }

    // 2. Líneas por Recuperar
    private fun generatePdfLineasRecuperar(data: List<ReporteLineaRecuperar>) {
        val doc = createPdfDocument()
        val page = startPage(doc)
        val canvas = page.canvas
        val paint = Paint()

        drawHeader(canvas, "LÍNEAS POR RECUPERAR", viewModel.empresaInfo.value)

        var y = 120f
        paint.isFakeBoldText = true
        paint.textSize = 10f
        canvas.drawText("Unidad", 40f, y, paint)
        canvas.drawText("SIM", 150f, y, paint)
        canvas.drawText("Cliente", 230f, y, paint)
        canvas.drawText("Susp.", 380f, y, paint)
        canvas.drawText("Días Rest.", 480f, y, paint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f

        paint.isFakeBoldText = false
        for (item in data) {
            canvas.drawText(item.unidad, 40f, y, paint)
            canvas.drawText(item.numeroSim ?: "-", 150f, y, paint)
            canvas.drawText(if(item.cliente.length > 20) item.cliente.substring(0,18) else item.cliente, 230f, y, paint)
            canvas.drawText(item.fechaSuspension, 380f, y, paint)
            canvas.drawText("${item.diasHastaRecuperacion}", 480f, y, paint)
            y += 15f
            if (y > 800) break
        }

        doc.finishPage(page)
        saveAndOpenPdf(doc, "Reporte_Recuperar_${System.currentTimeMillis()}.pdf")
    }

    // 3. Líneas Suspendidas
    private fun generatePdfLineasSuspendidas(data: List<ReporteLineaSuspendida>) {
        val doc = createPdfDocument()
        val page = startPage(doc)
        val canvas = page.canvas
        val paint = Paint()

        drawHeader(canvas, "LÍNEAS SUSPENDIDAS", viewModel.empresaInfo.value)

        var y = 120f
        paint.isFakeBoldText = true
        paint.textSize = 10f
        canvas.drawText("Unidad", 40f, y, paint)
        canvas.drawText("SIM", 200f, y, paint)
        canvas.drawText("Cliente", 300f, y, paint)
        canvas.drawText("Última Actividad", 450f, y, paint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f

        paint.isFakeBoldText = false
        for (item in data) {
            canvas.drawText(item.unidad, 40f, y, paint)
            canvas.drawText(item.numeroSim ?: "-", 200f, y, paint)
            canvas.drawText(if(item.cliente.length > 20) item.cliente.substring(0,18) else item.cliente, 300f, y, paint)
            canvas.drawText(item.ultimaFechaActiva ?: "N/A", 450f, y, paint)
            y += 15f
            if (y > 800) break
        }

        doc.finishPage(page)
        saveAndOpenPdf(doc, "Reporte_Suspendidas_${System.currentTimeMillis()}.pdf")
    }

    // 4. Renovaciones Vencidas
    private fun generatePdfRenovacionesVencidas(data: List<ReporteRenovacionVencida>) {
        val doc = createPdfDocument()
        val page = startPage(doc)
        val canvas = page.canvas
        val paint = Paint()

        drawHeader(canvas, "RENOVACIONES VENCIDAS", viewModel.empresaInfo.value)

        var y = 120f
        paint.isFakeBoldText = true
        paint.textSize = 10f
        canvas.drawText("Cliente", 40f, y, paint)
        canvas.drawText("Unidad", 180f, y, paint)
        canvas.drawText("Vencimiento", 350f, y, paint)
        canvas.drawText("Monto", 480f, y, paint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f

        paint.isFakeBoldText = false
        var total = 0.0
        for (item in data) {
            val cliente = if(item.cliente.length > 20) item.cliente.substring(0,18)+"..." else item.cliente
            canvas.drawText(cliente, 40f, y, paint)
            
            val unidad = if(item.unidad.length > 20) item.unidad.substring(0,18)+"..." else item.unidad
            canvas.drawText(unidad, 180f, y, paint)
            
            canvas.drawText(item.fechaVencimiento ?: "-", 350f, y, paint)
            canvas.drawText(String.format("$ %.2f", item.monto), 480f, y, paint)
            
            total += item.monto
            y += 15f
            if (y > 800) break
        }
        
        y += 10f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f
        paint.isFakeBoldText = true
        canvas.drawText("TOTAL VENCIDO: $ ${String.format("%.2f", total)}", 350f, y, paint)

        doc.finishPage(page)
        saveAndOpenPdf(doc, "Reporte_Renovaciones_${System.currentTimeMillis()}.pdf")
    }

    // 5. Detalle Facturas Servicios
    private fun generatePdfFacturasServicios(data: List<ReporteFacturaServicio>) {
        val doc = createPdfDocument()
        val page = startPage(doc)
        val canvas = page.canvas
        val paint = Paint()

        drawHeader(canvas, "RELACIÓN FACTURAS/SERVICIOS", viewModel.empresaInfo.value)

        var y = 120f
        paint.isFakeBoldText = true
        paint.textSize = 10f
        canvas.drawText("ID Servicio", 40f, y, paint)
        canvas.drawText("No. Factura", 150f, y, paint)
        canvas.drawText("Fecha Emisión", 280f, y, paint)
        canvas.drawText("Estado", 420f, y, paint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, Paint())
        y += 20f

        paint.isFakeBoldText = false
        for (item in data) {
            canvas.drawText("${item.idServicio}", 40f, y, paint)
            canvas.drawText(item.numeroFactura ?: "Sin Factura", 150f, y, paint)
            canvas.drawText(item.fechaEmision ?: "-", 280f, y, paint)
            canvas.drawText(item.estado?.uppercase() ?: "-", 420f, y, paint)
            y += 15f
            if (y > 800) break
        }

        doc.finishPage(page)
        saveAndOpenPdf(doc, "Reporte_Facturas_${System.currentTimeMillis()}.pdf")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
