package com.phoenixspark.connect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phoenixspark.connect.TableData
import com.phoenixspark.connect.data.Link

// Type Converters for complex types
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTableDataList(value: List<TableData>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toTableDataList(value: String?): List<TableData>? {
        return value?.let {
            val type = object : TypeToken<List<TableData>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromLinkList(value: List<Link>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLinkList(value: String?): List<Link>? {
        return value?.let {
            val type = object : TypeToken<List<Link>>() {}.type
            gson.fromJson(it, type)
        }
    }
}

@Entity(tableName = "bases")
@TypeConverters(Converters::class)
data class BaseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val state: String,
    val active: Boolean?,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "organizations")
@TypeConverters(Converters::class)
data class OrganizationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val contact: String?,
    val baseId: String,
    val webUrl: String?,
    val imageUrl: String?,
    val primaryColor: String?,
    val secondaryColor: String?,
    val textColor: String?,
    val type: String,
    val email: String,
    val buildingNumber: String?,
    val address: String?,
    val links: List<Link>?,
    val useTables: Boolean,
    val tableData: List<TableData>,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "base_details")
@TypeConverters(Converters::class)
data class BaseDetailsEntity(
    @PrimaryKey val id: String,
    val baseId: String,
    val imageUrl: String,
    val phone: String?,
    val email: String?,
    val commander: String?,
    val motto: String?,
    val population: Double?,
    val userId: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "page_cards")
@TypeConverters(Converters::class)
data class PageCardsEntity(
    @PrimaryKey val id: String,
    val baseId: String?,
    val orgId: String?,
    val showName: Boolean,
    val showMotto: Boolean,
    val showCommander: Boolean,
    val showPhone: Boolean,
    val showEmail: Boolean,
    val showTables: Boolean,
    val tableData: List<TableData>,
    val lastUpdated: Long = System.currentTimeMillis()
)
