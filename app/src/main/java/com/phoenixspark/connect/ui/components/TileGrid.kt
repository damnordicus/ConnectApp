package com.phoenixspark.connect.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenixspark.connect.TileConfig
import com.phoenixspark.connect.data.BaseTile
import com.phoenixspark.connect.data.TileType
import com.phoenixspark.connect.toAndroidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Changed from LazyVerticalGrid to regular composable
@Composable
fun TileGrid(
    tiles: List<BaseTile>,
    onTileClick: (BaseTile) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("TileGrid", "Rendering ${tiles.size} tiles")

    // Create rows of 2 tiles each
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tiles.chunked(2).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowTiles.forEach { tile ->
                    TileCard(
                        tile = tile,
                        onClick = { onTileClick(tile) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add empty space if odd number of tiles
                if (rowTiles.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TileCard(
    tile: BaseTile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() }
            .border(border = BorderStroke(1.dp, tile.color.copy(0.3f)), shape = MaterialTheme.shapes.medium)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tile.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tile.icon,
                        contentDescription = tile.title,
                        tint = tile.color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = tile.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

// Helper function to map tile type string to TileType enum
fun String.toTileType(): TileType {
    return when (this.lowercase()) {
        "text", "base_info", "base info" -> TileType.BASE_INFO
        "gate_hours", "gate hours" -> TileType.GATE_HOURS
        "emergency", "emergency_contacts" -> TileType.EMERGENCY_CONTACTS
        "commander", "commander_hotline" -> TileType.COMMANDER_HOTLINE
        "dining", "dining_facilities" -> TileType.DINING_FACILITIES
        "gym", "gym_hours" -> TileType.GYM_HOURS
        "events" -> TileType.EVENTS
        "map" -> TileType.MAP
        else -> TileType.BASE_INFO
    }
}

// Helper function to map tile type to icon
fun TileType.toIcon(): ImageVector {
    return when (this) {
        TileType.BASE_INFO -> Icons.Default.Info
        TileType.GATE_HOURS -> Icons.Default.Lock
        TileType.EMERGENCY_CONTACTS -> Icons.Default.Emergency
        TileType.COMMANDER_HOTLINE -> Icons.Default.Campaign
        TileType.DINING_FACILITIES -> Icons.Default.Restaurant
        TileType.GYM_HOURS -> Icons.Default.FitnessCenter
        TileType.EVENTS -> Icons.Default.Event
        TileType.MAP -> Icons.Default.Map
        TileType.CUSTOM -> Icons.Default.Info
    }
}

fun inferTileTypeFromTitle(title: String): TileType {
    val lowerTitle = title.lowercase()
    return when {
        lowerTitle.contains("base") && lowerTitle.contains("info") -> TileType.BASE_INFO
        lowerTitle.contains("gate") -> TileType.GATE_HOURS
        lowerTitle.contains("emergency") -> TileType.EMERGENCY_CONTACTS
        lowerTitle.contains("commander") -> TileType.COMMANDER_HOTLINE
        lowerTitle.contains("dining") || lowerTitle.contains("food") -> TileType.DINING_FACILITIES
        lowerTitle.contains("gym") || lowerTitle.contains("fitness") -> TileType.GYM_HOURS
        lowerTitle.contains("event") -> TileType.EVENTS
        lowerTitle.contains("map") -> TileType.MAP
        lowerTitle.contains("contact") -> TileType.EMERGENCY_CONTACTS
        else -> TileType.CUSTOM
    }
}

// Convert TileConfig list to BaseTile list
fun convertTileConfigToBaseTiles(tilesConfig: List<TileConfig>): List<BaseTile> {
    Log.d("BaseTiles", "Converting ${tilesConfig.size} TileConfig items to BaseTiles")
    Log.d("TileDetailScreen", "Looking for tile.id: '${tilesConfig[2].id}' (type: ${tilesConfig[2].id::class.simpleName}")

    return tilesConfig
        .filter { it.visible } // Only include visible tiles
        .map { config ->
            val tileType = inferTileTypeFromTitle(config.title)

            BaseTile(
                id = config.id,
                title = config.title,
                icon = tileType.toIcon(),
                color = config.color.toAndroidColor(),
                type = tileType
            )
        }
}

// Helper function to create default tiles
fun getDefaultBaseTiles(tilesConfig: List<TileConfig> = emptyList()): List<BaseTile> {
    Log.d("BaseTiles", "tilesConfig received: $tilesConfig")
    Log.d("BaseTiles", "tilesConfig size: ${tilesConfig.size}")

    // If we have tiles from the database, use those
    if (tilesConfig.isNotEmpty()) {
        Log.d("BaseTiles", "Using ${tilesConfig.size} tiles from database")
        return convertTileConfigToBaseTiles(tilesConfig)
    }

    // Otherwise, return default tiles
    Log.d("BaseTiles", "Using default tiles")
    return listOf(
        BaseTile(
            id = "base_info",
            title = "Base Info",
            icon = Icons.Default.Info,
            color = Color(0xFF6366F1),
            type = TileType.BASE_INFO
        ),
        BaseTile(
            id = "gate_hours",
            title = "Gate Hours",
            icon = Icons.Default.Lock,
            color = Color(0xFF10B981),
            type = TileType.GATE_HOURS
        ),
        BaseTile(
            id = "emergency",
            title = "Emergency Contacts",
            icon = Icons.Default.Emergency,
            color = Color(0xFFEF4444),
            type = TileType.EMERGENCY_CONTACTS
        ),
        BaseTile(
            id = "commander",
            title = "Commander's Hotline",
            icon = Icons.Default.Campaign,
            color = Color(0xFFF59E0B),
            type = TileType.COMMANDER_HOTLINE
        ),
        BaseTile(
            id = "dining",
            title = "Dining Facilities",
            icon = Icons.Default.Restaurant,
            color = Color(0xFF8B5CF6),
            type = TileType.DINING_FACILITIES
        ),
        BaseTile(
            id = "gym",
            title = "Gym Hours",
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFF3B82F6),
            type = TileType.GYM_HOURS
        ),
        BaseTile(
            id = "events",
            title = "Events",
            icon = Icons.Default.Event,
            color = Color(0xFFEC4899),
            type = TileType.EVENTS
        ),
        BaseTile(
            id = "map",
            title = "Base Map",
            icon = Icons.Default.Map,
            color = Color(0xFF14B8A6),
            type = TileType.MAP
        )
    )
}