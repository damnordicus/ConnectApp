package com.phoenixspark.connect

import android.R
import com.phoenixspark.connect.data.Link
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Query

// Data class that matches your Supabase table structure
data class BaseResponse(
    val id: String,
    val name: String,
    val city: String,
    val state: String,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val active: Boolean? = null,
    val created_at: String? = null
)
data class TableData(
    val id: String,
    val title: String,
    val headers: List<String>,
    val data: List<List<String>>
)

data class PageCardsResponse(
    val id: String,
    val base_id: String? = null,
    val org_id: String? = null,
    val show_name: Boolean,
    val show_motto: Boolean,
    val show_commander: Boolean,
    val show_phone: Boolean,
    val show_email: Boolean,
    val show_tables: Boolean,
    val table_data: List<TableData> = emptyList(),
)

data class OrganizationResponse(
    val id: String,
    val name: String,
    val description: String,
    val contact: String,
    val base_id: String,
    val web_url: String,
    val image_url: String,
    val primary_color: String,
    val secondary_color: String,
    val text_color: String,
    val type: String,
    val email: String,
    val building_number: String,
    val address: String,
    val links: List<Link>? = null,
    val use_tables: Boolean,
    val table_data: List<TableData> = emptyList(),
)

data class BaseDetailResponse(
    val id: String,
    val image_url: String,
    val phone: String? = null,
    val email: String? = null,
    val commander: String? = null,
    val motto: String? = null,
    val population: Number? = 0,
    val user_id: String,
    val show_name: Boolean,
)

// API interface for Supabase REST API
interface SupabaseApiService {
    @GET("base")
    suspend fun getAllBases(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String
    ): List<BaseResponse>

    @GET("baseDetails")
    suspend fun getAllBaseDetails(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("base_id") baseId: String,
        @Query("select") select: String = "*, base(*)"
    ): List<BaseDetailResponse>

    @GET("appFields")
    suspend fun getAllAppFields(
        @Header("apiKey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("base_id") baseId: String,
        @Query("select") select: String = "*"
    ): List<PageCardsResponse>

    @GET("organization")
    suspend fun getAllOrganizationsByBase(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("base_id") baseId: String,
        @Query("select") select: String = "*"
    ): List<OrganizationResponse>
}

// Supabase client configuration
object SupabaseClient {
    // Replace these with your actual Supabase credentials
    private const val SUPABASE_URL = "https://mbipidprgvippwpmljas.supabase.co/rest/v1/"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1iaXBpZHByZ3ZpcHB3cG1samFzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTczNTA3NjMsImV4cCI6MjA3MjkyNjc2M30.nxFMvYbYcVjb3mRtT8bZrLYOoNnVMj5eL9IPYozX27k"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: SupabaseApiService = retrofit.create(SupabaseApiService::class.java)

    // Helper function to get bases
    suspend fun getBases(): List<BaseResponse> {
        return try {
            apiService.getAllBases(
                apiKey = SUPABASE_ANON_KEY,
                authorization = "Bearer $SUPABASE_ANON_KEY"
            )
        } catch (e: Exception) {
            println("Error fetching bases: ${e.message}")
            emptyList()
        }
    }

    suspend fun getBaseDetails(baseId: String): List<BaseDetailResponse>{
        return try {
            apiService.getAllBaseDetails(
                apiKey = SUPABASE_ANON_KEY,
                authorization = "Bearer $SUPABASE_ANON_KEY",
                baseId = "eq.$baseId"
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAppFields(baseId: String): List<PageCardsResponse> {
        return try {
            apiService.getAllAppFields(
                apiKey = SUPABASE_ANON_KEY,
                authorization = "Bearer $SUPABASE_ANON_KEY",
                baseId = "eq.$baseId"
            )
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun getOrganizationsByBase(baseId: String): List<OrganizationResponse> {
        return try {
            // Make sure you're using the correct column name and filtering properly
            val response = apiService.getAllOrganizationsByBase(
                apiKey = SUPABASE_ANON_KEY,
                authorization = "Bearer $SUPABASE_ANON_KEY",
                baseId = "eq.$baseId"  // Supabase REST API uses "eq." prefix for exact match
            )

            println("DEBUG: Supabase query for base_id=$baseId returned ${response.size} results")
            response
        } catch (e: Exception) {
            println("DEBUG: Supabase query failed: ${e.message}")
            throw e
        }
    }
//    suspend fun getOrganizationsByBase(id: String): List<OrganizationResponse> {
//        return try {
//            apiService.getAllOrganizationsByBase(
//                apiKey = SUPABASE_ANON_KEY,
//                authorization = "Bearer $SUPABASE_ANON_KEY"
//            )
//        } catch (e: Exception) {
//            println("Error fetching bases: ${e.message}")
//            emptyList()
//        }
//    }

}