package com.example.ksaturno

import com.example.ksaturno.categories.Category
import com.example.ksaturno.categories.CreateCategoryRequest
import com.example.ksaturno.clients.Client
import com.example.ksaturno.units.CreateUnitRequest
import com.example.ksaturno.units.Unit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @DELETE("categorias/eliminar.php/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<ApiResponse>

    // Units
    @GET("unidades/listar.php")
    suspend fun getUnits(): Response<List<Unit>>

    @POST("unidades/grabar.php")
    suspend fun createUnit(@Body request: CreateUnitRequest): Response<ApiResponse>

    @PUT("unidades/actualizar.php/{id}")
    suspend fun updateUnit(@Path("id") id: Int, @Body unit: Unit): Response<ApiResponse>

    @DELETE("unidades/eliminar.php/{id}")
    suspend fun deleteUnit(@Path("id") id: Int): Response<ApiResponse>

    // Clients
    @GET("clientes/listar.php")
    suspend fun getClients(): Response<List<Client>>
}
