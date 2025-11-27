package com.example.ksaturno

import com.example.ksaturno.categories.Category
import com.example.ksaturno.categories.CategoryIdBody
import com.example.ksaturno.categories.CreateCategoryRequest
import com.example.ksaturno.checklist.ChecklistItem
import com.example.ksaturno.checklist.ChecklistItemIdBody
import com.example.ksaturno.checklist.CompletedChecklistItem
import com.example.ksaturno.checklist.CreateChecklistItemRequest
import com.example.ksaturno.checklist.CreateListaVerificacionRequest
import com.example.ksaturno.clients.Client
import com.example.ksaturno.clients.ClientIdBody
import com.example.ksaturno.clients.CreateClientRequest
import com.example.ksaturno.empresa.CreateEmpresaRequest
import com.example.ksaturno.empresa.Empresa
import com.example.ksaturno.empresa.EmpresaIdBody
import com.example.ksaturno.evidencias.CreateEvidenciaRequest
import com.example.ksaturno.facturas.Factura
import com.example.ksaturno.facturas.GenerateInvoiceRequest
import com.example.ksaturno.home.RenewalAlert
import com.example.ksaturno.instalaciones.CreateInstalacionRequest
import com.example.ksaturno.instalaciones.Instalacion
import com.example.ksaturno.instalaciones.InstalacionIdBody
import com.example.ksaturno.pagos.CreatePaymentRequest
import com.example.ksaturno.servicios.CreateServicioRequest
import com.example.ksaturno.servicios.Servicio
import com.example.ksaturno.servicios.ServicioIdBody
import com.example.ksaturno.technicians.CreateTechnicianRequest
import com.example.ksaturno.technicians.Technician
import com.example.ksaturno.technicians.TechnicianIdBody
import com.example.ksaturno.units.CreateUnitRequest
import com.example.ksaturno.units.Unit
import com.example.ksaturno.units.UnitIdBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest):
            Response<LoginResponse>

    // Categories
    @GET("categorias/listar.php")
    suspend fun getCategories(): Response<List<Category>>

    @POST("categorias/grabar.php")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Response<ApiResponse>

    @PUT("categorias/actualizar.php/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body category: Category): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "categorias/eliminar.php", hasBody = true)
    suspend fun deleteCategory(@Body body: CategoryIdBody): Response<ApiResponse>

    // Units
    @GET("unidades/listar.php")
    suspend fun getUnits(): Response<List<Unit>>

    @GET("unidades/filtrarporcliente.php")
    suspend fun getUnitsByClient(@Query("id_cliente") clientId: Int): Response<List<Unit>>

    @POST("unidades/grabar.php")
    suspend fun createUnit(@Body request: CreateUnitRequest): Response<ApiResponse>

    @PUT("unidades/actualizar.php/{id}")
    suspend fun updateUnit(@Path("id") id: Int, @Body unit: Unit): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "unidades/eliminar.php", hasBody = true)
    suspend fun deleteUnit(@Body body: UnitIdBody): Response<ApiResponse>

    // Clients
    @GET("clientes/listar.php")
    suspend fun getClients(): Response<List<Client>>

    @POST("clientes/grabar.php")
    suspend fun createClient(@Body request: CreateClientRequest): Response<ApiResponse>

    @PUT("clientes/actualizar.php/{id}")
    suspend fun updateClient(@Path("id") id: Int, @Body client: Client): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "clientes/eliminar.php", hasBody = true)
    suspend fun deleteClient(@Body body: ClientIdBody): Response<ApiResponse>

    // Technicians
    @GET("tecnicos/listar.php")
    suspend fun getTechnicians(): Response<List<Technician>>

    @POST("tecnicos/grabar.php")
    suspend fun createTechnician(@Body request: CreateTechnicianRequest): Response<ApiResponse>

    @PUT("tecnicos/actualizar.php/{id}")
    suspend fun updateTechnician(@Path("id") id: Int, @Body technician: Technician): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "tecnicos/eliminar.php", hasBody = true)
    suspend fun deleteTechnician(@Body body: TechnicianIdBody): Response<ApiResponse>

    // Checklist Items
    @GET("items_lista_verificacion/listar.php")
    suspend fun getChecklistItems(): Response<List<ChecklistItem>>

    @POST("items_lista_verificacion/grabar.php")
    suspend fun createChecklistItem(@Body request: CreateChecklistItemRequest): Response<ApiResponse>

    @PUT("items_lista_verificacion/actualizar.php/{id}")
    suspend fun updateChecklistItem(@Path("id") id: Int, @Body item: ChecklistItem): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "items_lista_verificacion/eliminar.php", hasBody = true)
    suspend fun deleteChecklistItem(@Body body: ChecklistItemIdBody): Response<ApiResponse>

    // Servicios
    @GET("servicios/listar.php")
    suspend fun getServicios(): Response<List<Servicio>>

    // NEW: Filter services by client directly from the API
    @GET("servicios/filtrarporcliente.php")
    suspend fun getServiciosByClient(@Query("id_cliente") clientId: Int): Response<List<Servicio>>

    @POST("servicios/grabar.php")
    suspend fun createServicio(@Body request: CreateServicioRequest): Response<ApiResponse>

    @PUT("servicios/actualizar.php")
    suspend fun updateServicio(@Query("id") id: Int, @Body servicio: Servicio): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "servicios/eliminar.php", hasBody = true)
    suspend fun deleteServicio(@Body body: ServicioIdBody): Response<ApiResponse>

    // Installation Flow
    @GET("instalaciones/listar.php")
    suspend fun getInstalaciones(): Response<List<Instalacion>>

    @POST("instalaciones/grabar.php")
    suspend fun createInstalacion(@Body request: CreateInstalacionRequest): Response<ApiResponse>

    @PUT("instalaciones/actualizar.php/{id}")
    suspend fun updateInstalacion(@Path("id") id: Int, @Body instalacion: Instalacion): Response<ApiResponse>

    @POST("instalaciones/eliminar.php")
    suspend fun deleteInstalacion(@Body body: InstalacionIdBody): Response<ApiResponse>

    @POST("items_lista_verificacion/grabar_respuesta.php") 
    suspend fun saveChecklistItem(@Body request: CreateListaVerificacionRequest): Response<ApiResponse>

    @GET("instalaciones/evidencias_pendientes.php")
    suspend fun getEvidencePendingItems(@Query("id_instalacion") installationId: Int): Response<List<CompletedChecklistItem>>

    @POST("evidencias/grabar.php")
    suspend fun saveEvidence(@Body request: CreateEvidenciaRequest): Response<ApiResponse>
    
    // Empresa
    @GET("empresas/listar.php")
    suspend fun getEmpresas(): Response<List<Empresa>>

    @POST("empresas/grabar.php")
    suspend fun createEmpresa(@Body request: CreateEmpresaRequest): Response<ApiResponse>

    @PUT("empresas/actualizar.php")
    suspend fun updateEmpresa(@Body empresa: Empresa): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "empresas/eliminar.php", hasBody = true)
    suspend fun deleteEmpresa(@Body body: EmpresaIdBody): Response<ApiResponse>

    // Facturas - Generación automática
    @POST("facturas/generar_desde_instalacion.php")
    suspend fun generateInvoiceFromInstallation(@Body request: GenerateInvoiceRequest): Response<ApiResponse>

    // Facturas - Finanzas por Cliente
    @GET("facturas/listar_por_cliente.php")
    suspend fun getFacturasByClient(
        @Query("id_cliente") clientId: Int,
        @Query("estado") estado: String
    ): Response<List<Factura>>
    
    // Renovaciones
    @GET("reportes/alerta_renovaciones.php")
    suspend fun getRenewalAlerts(): Response<List<RenewalAlert>>
    
    // Pagos
    @POST("pagos/registrar_pago_factura.php")
    suspend fun registerPayment(@Body request: CreatePaymentRequest): Response<ApiResponse>
}
