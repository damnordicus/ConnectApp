package com.phoenixspark.connect.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.phoenixspark.connect.TableData
import com.phoenixspark.connect.TileConfig
import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val label: String,
    val link: String
)

data class Organization(
    val id: String,
    val name: String,
    val type: OrganizationType,
    val description: String?,
    val contact: String?,
    val baseId: String,
    val webUrl: String? = null,
    val primaryColor: String? = null,
    val secondaryColor: String? = null,
    val textColor: String? = null,
    val imageUrl: String? = null,
    val email: String,
    val building_number: String? = null,
    val address: String? = null,
    val links: List<Link>? = null,
    val useTable: Boolean,
    val tableData: List<TableData> = emptyList(),
)

data class BaseDetails(
    val id: String,
    val imageUrl: String,
    val phone: String? = null,
    val email: String? = null,
    val commander: String? = null,
    val motto: String? = null,
    val population: Number? = 0,
    val userId: String,
)

data class PageCards(
    val id: String,
    val base_id: String? = null,
    val org_id: String? = null,
    val show_name: Boolean,
    val show_motto: Boolean,
    val show_commander: Boolean,
    val show_phone: Boolean,
    val show_email: Boolean,
    val show_tables: Boolean,
    val tableData: List<TableData> = emptyList(),
    val tilesConfig: List<TileConfig> = emptyList()
)

enum class OrganizationType(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    WING("Wing", Icons.Default.Flight, Color(0xFF3B82F6)),
    GROUP("Group", Icons.Default.Groups, Color(0xFF10B981)),
    SQUADRON("Squadron", Icons.Default.Shield, Color(0xFF8B5CF6)),
    AGENCY("Agency", Icons.Default.Business, Color(0xFFF59E0B)),
    ORGANIZATION("Organization", Icons.Default.AccountBalance, Color(0xFFEF4444)),
    SUPPORT("Support", Icons.Default.Build, Color(0xFF6B7280))
}

// New data class for tiles
data class BaseTile(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val type: TileType
)

enum class TileType {
    BASE_INFO,
    GATE_HOURS,
    EMERGENCY_CONTACTS,
    COMMANDER_HOTLINE,
    DINING_FACILITIES,
    GYM_HOURS,
    EVENTS,
    MAP
}