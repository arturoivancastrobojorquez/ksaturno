package com.example.ksaturno

import com.example.ksaturno.categories.Category
import com.example.ksaturno.categories.CategoryIdBody
import com.example.ksaturno.categories.CreateCategoryRequest
import com.example.ksaturno.checklist.ChecklistItem
import com.example.ksaturno.checklist.ChecklistItemIdBody
import com.example.ksaturno.checklist.CreateChecklistItemRequest
import com.example.ksaturno.clients.Client
import com.example.ksaturno.clients.ClientIdBody
import com.example.ksaturno.clients.CreateClientRequest
import com.example.ksaturno.technicians.CreateTechnicianRequest
import com.example.ksaturno.technicians.Technician
import com.example.ksaturno.technicians.TechnicianIdBody
import com.example.ksaturno.units.CreateUnitRequest
import com.example.ksaturno.units.Unit
import com.example.ksaturno.units.UnitIdBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

}
