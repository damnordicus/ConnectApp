package com.phoenixspark.connect.ui.components

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
import com.phoenixspark.connect.data.BaseTile
import com.phoenixspark.connect.data.TileType
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
    // Create rows of 2 tiles each
    Column(
        modifier = modifier
            .fillMaxWidth()
//            .background(color = MaterialTheme.colorScheme.background)
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
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            tile.color.copy(alpha = 0.1f),
//                            tile.color.copy(alpha = 0.05f)
//                        )
//                    )
//                )
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

// Helper function to create default tiles
fun getDefaultBaseTiles(): List<BaseTile> {
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