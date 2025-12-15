package com.phoenixspark.connect.data.repository

import android.content.Context
import com.phoenixspark.connect.*
import com.phoenixspark.connect.data.Link
import com.phoenixspark.connect.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val baseDao = database.baseDao()
    private val organizationDao = database.organizationDao()
    private val baseDetailsDao = database.baseDetailsDao()
    private val pageCardsDao = database.pageCardsDao()

    // Get bases from local database
    fun getAllBases(): Flow<List<Base>> {
        return baseDao.getAllBases().map { entities ->
            entities.map { it.toBase() }
        }
    }

    // Get organizations from local database
    fun getOrganizationsByBase(baseId: String): Flow<List<OrganizationResponse>> {
        return organizationDao.getOrganizationsByBase(baseId).map { entities ->
            entities.map { it.toOrganizationResponse() }
        }
    }

    // Sync data from server and save locally
    suspend fun syncAllData(savedBases: List<Base>) {
        val currentTime = System.currentTimeMillis()

        // Save bases
        val baseEntities = savedBases.map { base ->
            BaseEntity(
                id = base.id,
                name = base.name,
                city = base.city,
                state = base.state,
                active = base.active,
                lastUpdated = currentTime
            )
        }
        baseDao.insertBases(baseEntities)

        // For each base, fetch and save organizations, details, and fields
        savedBases.forEach { base ->
            try {
                // Fetch organizations
                val organizations = SupabaseClient.getOrganizationsByBase(base.id)
                val orgEntities = organizations.map { org ->
                    OrganizationEntity(
                        id = org.id,
                        name = org.name,
                        description = org.description,
                        contact = org.contact,
                        baseId = org.base_id,
                        webUrl = org.web_url,
                        imageUrl = org.image_url,
                        primaryColor = org.primary_color,
                        secondaryColor = org.secondary_color,
                        textColor = org.text_color,
                        type = org.type,
                        email = org.email,
                        buildingNumber = org.building_number,
                        address = org.address,
                        links = org.links,
                        useTables = org.use_tables,
                        tableData = org.table_data,
                        lastUpdated = currentTime
                    )
                }
                organizationDao.insertOrganizations(orgEntities)

                // Fetch base details
                val details = SupabaseClient.getBaseDetails(base.id)
                details.firstOrNull()?.let { detail ->
                    val detailEntity = BaseDetailsEntity(
                        id = detail.id,
                        baseId = base.id,
                        imageUrl = detail.image_url,
                        phone = detail.phone,
                        email = detail.email,
                        commander = detail.commander,
                        motto = detail.motto,
                        population = detail.population?.toDouble(),
                        userId = detail.user_id,
                        lastUpdated = currentTime
                    )
                    baseDetailsDao.insertBaseDetails(detailEntity)
                }

                // Fetch app fields
                val fields = SupabaseClient.getAppFields(base.id)
                fields.firstOrNull()?.let { field ->
                    val fieldEntity = PageCardsEntity(
                        id = field.id,
                        baseId = field.base_id,
                        orgId = field.org_id,
                        showName = field.show_name,
                        showMotto = field.show_motto,
                        showCommander = field.show_commander,
                        showPhone = field.show_phone,
                        showEmail = field.show_email,
                        showTables = field.show_tables,
                        tableData = field.table_data,
                        lastUpdated = currentTime
                    )
                    pageCardsDao.insertPageCards(fieldEntity)
                }
            } catch (e: Exception) {
                println("Error syncing data for base ${base.name}: ${e.message}")
            }
        }
    }

    // Get base details from local database
    suspend fun getBaseDetails(baseId: String): BaseDetailResponse? {
        return baseDetailsDao.getBaseDetails(baseId)?.toBaseDetailResponse()
    }

    // Get page cards from local database
    suspend fun getPageCards(baseId: String): PageCardsResponse? {
        return pageCardsDao.getPageCardsByBase(baseId)?.toPageCardsResponse()
    }

    // Get organizations sync (for non-Flow use)
    suspend fun getOrganizationsByBaseSync(baseId: String): List<OrganizationResponse> {
        return organizationDao.getOrganizationsByBaseSync(baseId).map { it.toOrganizationResponse() }
    }

    // Get last update time
    suspend fun getLastUpdateTime(): Long? {
        return baseDao.getLastUpdateTime() ?: organizationDao.getLastUpdateTime()
    }

    // Check if we have cached data
    suspend fun hasCachedData(): Boolean {
        return baseDao.getLastUpdateTime() != null
    }
}

// Extension functions to convert entities to response models
private fun BaseEntity.toBase(): Base {
    return Base(
        id = id,
        name = name,
        city = city,
        state = state,
        active = active
    )
}

private fun OrganizationEntity.toOrganizationResponse(): OrganizationResponse {
    return OrganizationResponse(
        id = id,
        name = name,
        description = description ?: "",
        contact = contact ?: "",
        base_id = baseId,
        web_url = webUrl ?: "",
        image_url = imageUrl ?: "",
        primary_color = primaryColor ?: "",
        secondary_color = secondaryColor ?: "",
        text_color = textColor ?: "",
        type = type,
        email = email,
        building_number = buildingNumber ?: "",
        address = address ?: "",
        links = links,
        use_tables = useTables,
        table_data = tableData
    )
}

private fun BaseDetailsEntity.toBaseDetailResponse(): BaseDetailResponse {
    return BaseDetailResponse(
        id = id,
        image_url = imageUrl,
        phone = phone,
        email = email,
        commander = commander,
        motto = motto,
        population = population,
        user_id = userId,
        show_name = true
    )
}

private fun PageCardsEntity.toPageCardsResponse(): PageCardsResponse {
    return PageCardsResponse(
        id = id,
        base_id = baseId,
        org_id = orgId,
        show_name = showName,
        show_motto = showMotto,
        show_commander = showCommander,
        show_phone = showPhone,
        show_email = showEmail,
        show_tables = showTables,
        table_data = tableData
    )
}
