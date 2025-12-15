package com.phoenixspark.connect.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BaseDao {
    @Query("SELECT * FROM bases ORDER BY name ASC")
    fun getAllBases(): Flow<List<BaseEntity>>

    @Query("SELECT * FROM bases WHERE id = :baseId")
    suspend fun getBaseById(baseId: String): BaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBases(bases: List<BaseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBase(base: BaseEntity)

    @Query("DELETE FROM bases")
    suspend fun deleteAll()

    @Query("SELECT lastUpdated FROM bases ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdateTime(): Long?
}

@Dao
interface OrganizationDao {
    @Query("SELECT * FROM organizations WHERE baseId = :baseId ORDER BY name ASC")
    fun getOrganizationsByBase(baseId: String): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations WHERE baseId = :baseId ORDER BY name ASC")
    suspend fun getOrganizationsByBaseSync(baseId: String): List<OrganizationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganizations(organizations: List<OrganizationEntity>)

    @Query("DELETE FROM organizations WHERE baseId = :baseId")
    suspend fun deleteByBase(baseId: String)

    @Query("DELETE FROM organizations")
    suspend fun deleteAll()

    @Query("SELECT lastUpdated FROM organizations ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdateTime(): Long?
}

@Dao
interface BaseDetailsDao {
    @Query("SELECT * FROM base_details WHERE baseId = :baseId")
    suspend fun getBaseDetails(baseId: String): BaseDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseDetails(details: BaseDetailsEntity)

    @Query("DELETE FROM base_details WHERE baseId = :baseId")
    suspend fun deleteByBase(baseId: String)

    @Query("DELETE FROM base_details")
    suspend fun deleteAll()
}

@Dao
interface PageCardsDao {
    @Query("SELECT * FROM page_cards WHERE baseId = :baseId")
    suspend fun getPageCardsByBase(baseId: String): PageCardsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageCards(pageCards: PageCardsEntity)

    @Query("DELETE FROM page_cards WHERE baseId = :baseId")
    suspend fun deleteByBase(baseId: String)

    @Query("DELETE FROM page_cards")
    suspend fun deleteAll()
}
