package com.phoenixspark.connect

import android.R
import androidx.compose.ui.graphics.Color
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

data class TileConfig(
    val id: String,
    val type: String,
    val color: String,
    val title: String,
    val content: Any? = null,  // Can be String, List, or other types
    val visible: Boolean = true
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
    val tiles_config: List<TileConfig> = emptyList()
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

// Helper function to convert Tailwind CSS colors to Android Colors
fun String.toAndroidColor(): Color {
    // Remove opacity suffix (e.g., "/20")
    val baseColor = this.split("/")[0]

    return when {
        baseColor.contains("slate") -> when {
            baseColor.contains("50") -> Color(0xFFF8FAFC)
            baseColor.contains("100") -> Color(0xFFF1F5F9)
            baseColor.contains("200") -> Color(0xFFE2E8F0)
            baseColor.contains("300") -> Color(0xFFCBD5E1)
            baseColor.contains("400") -> Color(0xFF94A3B8)
            baseColor.contains("500") -> Color(0xFF64748B)
            baseColor.contains("600") -> Color(0xFF475569)
            baseColor.contains("700") -> Color(0xFF334155)
            baseColor.contains("800") -> Color(0xFF1E293B)
            baseColor.contains("900") -> Color(0xFF0F172A)
            else -> Color(0xFF64748B)
        }
        baseColor.contains("sky") -> when {
            baseColor.contains("50") -> Color(0xFFF0F9FF)
            baseColor.contains("100") -> Color(0xFFE0F2FE)
            baseColor.contains("200") -> Color(0xFFBAE6FD)
            baseColor.contains("300") -> Color(0xFF7DD3FC)
            baseColor.contains("400") -> Color(0xFF38BDF8)
            baseColor.contains("500") -> Color(0xFF0EA5E9)
            baseColor.contains("600") -> Color(0xFF0284C7)
            baseColor.contains("700") -> Color(0xFF0369A1)
            baseColor.contains("800") -> Color(0xFF075985)
            baseColor.contains("900") -> Color(0xFF0C4A6E)
            else -> Color(0xFF0EA5E9)
        }
        baseColor.contains("blue") -> when {
            baseColor.contains("50") -> Color(0xFFEFF6FF)
            baseColor.contains("100") -> Color(0xFFDBEAFE)
            baseColor.contains("200") -> Color(0xFFBFDBFE)
            baseColor.contains("300") -> Color(0xFF93C5FD)
            baseColor.contains("400") -> Color(0xFF60A5FA)
            baseColor.contains("500") -> Color(0xFF3B82F6)
            baseColor.contains("600") -> Color(0xFF2563EB)
            baseColor.contains("700") -> Color(0xFF1D4ED8)
            baseColor.contains("800") -> Color(0xFF1E40AF)
            baseColor.contains("900") -> Color(0xFF1E3A8A)
            else -> Color(0xFF3B82F6)
        }
        baseColor.contains("rose") || baseColor.contains("red") -> when {
            baseColor.contains("50") -> Color(0xFFFFF1F2)
            baseColor.contains("100") -> Color(0xFFFFE4E6)
            baseColor.contains("200") -> Color(0xFFFECDD3)
            baseColor.contains("300") -> Color(0xFFFDA4AF)
            baseColor.contains("400") -> Color(0xFFFB7185)
            baseColor.contains("500") -> Color(0xFFF43F5E)
            baseColor.contains("600") -> Color(0xFFE11D48)
            baseColor.contains("700") -> Color(0xFFBE123C)
            baseColor.contains("800") -> Color(0xFF9F1239)
            baseColor.contains("900") -> Color(0xFF881337)
            else -> Color(0xFFEF4444)
        }
        baseColor.contains("green") || baseColor.contains("emerald") -> when {
            baseColor.contains("50") -> Color(0xFFF0FDF4)
            baseColor.contains("100") -> Color(0xFFDCFCE7)
            baseColor.contains("200") -> Color(0xFFBBF7D0)
            baseColor.contains("300") -> Color(0xFF86EFAC)
            baseColor.contains("400") -> Color(0xFF4ADE80)
            baseColor.contains("500") -> Color(0xFF22C55E)
            baseColor.contains("600") -> Color(0xFF16A34A)
            baseColor.contains("700") -> Color(0xFF15803D)
            baseColor.contains("800") -> Color(0xFF166534)
            baseColor.contains("900") -> Color(0xFF14532D)
            else -> Color(0xFF10B981)
        }
        baseColor.contains("yellow") || baseColor.contains("amber") -> when {
            baseColor.contains("50") -> Color(0xFFFFFBEB)
            baseColor.contains("100") -> Color(0xFFFEF3C7)
            baseColor.contains("200") -> Color(0xFFFDE68A)
            baseColor.contains("300") -> Color(0xFFFCD34D)
            baseColor.contains("400") -> Color(0xFFFBBF24)
            baseColor.contains("500") -> Color(0xFFF59E0B)
            baseColor.contains("600") -> Color(0xFFD97706)
            baseColor.contains("700") -> Color(0xFFB45309)
            baseColor.contains("800") -> Color(0xFF92400E)
            baseColor.contains("900") -> Color(0xFF78350F)
            else -> Color(0xFFF59E0B)
        }
        baseColor.contains("purple") || baseColor.contains("violet") -> when {
            baseColor.contains("50") -> Color(0xFFFAF5FF)
            baseColor.contains("100") -> Color(0xFFF3E8FF)
            baseColor.contains("200") -> Color(0xFFE9D5FF)
            baseColor.contains("300") -> Color(0xFFD8B4FE)
            baseColor.contains("400") -> Color(0xFFC084FC)
            baseColor.contains("500") -> Color(0xFFA855F7)
            baseColor.contains("600") -> Color(0xFF9333EA)
            baseColor.contains("700") -> Color(0xFF7E22CE)
            baseColor.contains("800") -> Color(0xFF6B21A8)
            baseColor.contains("900") -> Color(0xFF581C87)
            else -> Color(0xFF8B5CF6)
        }
        baseColor.contains("pink") -> when {
            baseColor.contains("50") -> Color(0xFFFDF2F8)
            baseColor.contains("100") -> Color(0xFFFCE7F3)
            baseColor.contains("200") -> Color(0xFFFBCFE8)
            baseColor.contains("300") -> Color(0xFFF9A8D4)
            baseColor.contains("400") -> Color(0xFFF472B6)
            baseColor.contains("500") -> Color(0xFFEC4899)
            baseColor.contains("600") -> Color(0xFFDB2777)
            baseColor.contains("700") -> Color(0xFFBE185D)
            baseColor.contains("800") -> Color(0xFF9D174D)
            baseColor.contains("900") -> Color(0xFF831843)
            else -> Color(0xFFEC4899)
        }
        baseColor.contains("indigo") -> when {
            baseColor.contains("50") -> Color(0xFFEEF2FF)
            baseColor.contains("100") -> Color(0xFFE0E7FF)
            baseColor.contains("200") -> Color(0xFFC7D2FE)
            baseColor.contains("300") -> Color(0xFFA5B4FC)
            baseColor.contains("400") -> Color(0xFF818CF8)
            baseColor.contains("500") -> Color(0xFF6366F1)
            baseColor.contains("600") -> Color(0xFF4F46E5)
            baseColor.contains("700") -> Color(0xFF4338CA)
            baseColor.contains("800") -> Color(0xFF3730A3)
            baseColor.contains("900") -> Color(0xFF312E81)
            else -> Color(0xFF6366F1)
        }
        baseColor.contains("teal") || baseColor.contains("cyan") -> when {
            baseColor.contains("50") -> Color(0xFFF0FDFA)
            baseColor.contains("100") -> Color(0xFFCCFBF1)
            baseColor.contains("200") -> Color(0xFF99F6E4)
            baseColor.contains("300") -> Color(0xFF5EEAD4)
            baseColor.contains("400") -> Color(0xFF2DD4BF)
            baseColor.contains("500") -> Color(0xFF14B8A6)
            baseColor.contains("600") -> Color(0xFF0D9488)
            baseColor.contains("700") -> Color(0xFF0F766E)
            baseColor.contains("800") -> Color(0xFF115E59)
            baseColor.contains("900") -> Color(0xFF134E4A)
            else -> Color(0xFF14B8A6)
        }
        else -> Color(0xFF6366F1) // Default indigo
    }
}

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
}