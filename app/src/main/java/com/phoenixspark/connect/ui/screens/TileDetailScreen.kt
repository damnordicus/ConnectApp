package com.phoenixspark.connect.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phoenixspark.connect.TileConfig
import com.phoenixspark.connect.TileSection
import com.phoenixspark.connect.data.*
import com.phoenixspark.connect.ui.components.SectionHeader
import com.phoenixspark.connect.ui.components.TableCard
import com.phoenixspark.connect.TableData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileDetailScreen(
    tile: BaseTile,
    baseDetails: BaseDetails,
    fields: PageCards?,
    onBackPressed: () -> Unit
) {
    // Find the tile config for this tile
    Log.d("TileDetailScreen", "=== Tile Detail Screen Debug ===")
    Log.d("TileDetailScreen", "tile.id = '${tile.id}' (type: ${tile.id::class.simpleName})")
    Log.d("TileDetailScreen", "tile.title = '${tile.title}'")
    Log.d("TileDetailScreen", "tile.type = ${tile.type}")

    Log.d("TileDetailScreen", "Available tiles in tilesConfig:")
    fields?.tilesConfig?.forEach { config ->
        Log.d("TileDetailScreen", "  - TileConfig.id='${config.id}' (type: ${config.id::class.simpleName}), title='${config.title}', sections=${config.sections.size}")
    }

    val tileConfig = fields?.tilesConfig?.find { it.id.toString() == tile.id.toString() }

    Log.d("TileDetailScreen", "Match found: ${tileConfig != null}")
    if (tileConfig != null) {
        Log.d("TileDetailScreen", "Found tileConfig with ${tileConfig.sections.size} sections")
        tileConfig.sections.forEachIndexed { index, section ->
            Log.d("TileDetailScreen", "  Section $index: type='${section.type}'")
        }
    } else {
        Log.e("TileDetailScreen", "ERROR: Could not find matching tileConfig for tile.id='${tile.id}'")
    }
    Log.d("TileDetailScreen", "=== End Debug ===")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

                Text(
                    text = tile.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Content based on sections
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 80.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Log.d("TileDetailScreen", "=== RENDERING LOGIC ===")
            Log.d("TileDetailScreen", "tileConfig != null: ${tileConfig != null}")
            Log.d("TileDetailScreen", "tileConfig.sections.isNotEmpty(): ${tileConfig?.sections?.isNotEmpty()}")

            if (tileConfig != null && tileConfig.sections.isNotEmpty()) {
                Log.d("TileDetailScreen", "RENDERING ${tileConfig.sections.size} SECTIONS")
                // Render sections in order
                tileConfig.sections.forEach { section ->
                    item {
                        Log.d("TileDetailScreen", "Rendering section: type='${section.type}'")
                        TileSectionRenderer(
                            section = section,
                            tileColor = tile.color,
                            fields = fields,
                        )
                    }
                }
            } else {
                Log.d("TileDetailScreen", "FALLING BACK TO OLD TILE TYPE LOGIC (tile.type=${tile.type})")
                // Fallback to old logic or placeholder
                when (tile.type) {
                    TileType.BASE_INFO -> {
                        // Commander
                        if (fields?.show_commander == true && baseDetails.commander != null) {
                            item {
                                InfoCard(
                                    icon = Icons.Default.Person,
                                    label = "Commander",
                                    value = baseDetails.commander,
                                    color = tile.color
                                )
                            }
                        }

                        // Motto
                        if (fields?.show_motto == true && baseDetails.motto != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF0F9FF)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.FormatQuote,
                                                contentDescription = "Motto",
                                                tint = tile.color,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Base Motto",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                        Text(
                                            text = "\"${baseDetails.motto}\"",
                                            fontSize = 16.sp,
                                            color = Color(0xFF475569),
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            lineHeight = 24.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Population
                        baseDetails.population?.let { population ->
                            item {
                                InfoCard(
                                    icon = Icons.Default.Groups,
                                    label = "Population",
                                    value = population.toString(),
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        // Contact Info
                        item {
                            ContactInfoCard(
                                baseDetails = baseDetails,
                                fields = fields
                            )
                        }
                    }

                    else -> {
                        item {
                            PlaceholderCard(
                                icon = tile.icon,
                                title = tile.title,
                                message = "This feature is coming soon.",
                                color = tile.color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TileSectionRenderer(
    section: TileSection,
    fields: PageCards?,
    tileColor: Color
) {
    val gson = Gson()

    Log.d("TileSectionRenderer", "Rendering section type: ${section.type}")
    Log.d("TileSectionRenderer", "Section content: ${section.content}")

    when (section.type.lowercase()) {
        "text" -> {
            TextSection(
                content = section.content.toString(),
                color = tileColor
            )
        }

        "image" -> {
//            try {
                // Content could be a string URL or an object with url/caption
                val imageUrl = when (section.content) {
                    is String -> section.content
                    is Map<*, *> -> section.content["url"]?.toString() ?: ""
                    else -> {
                        val contentStr = gson.toJson(section.content)
                        val imageObj = gson.fromJson(contentStr, Map::class.java)
                        imageObj["url"]?.toString() ?: ""
                    }
                }

                ImageSection(url = imageUrl)
//            } catch (e: Exception) {
//                Log.e("TileSectionRenderer", "Error parsing image section: ${e.message}")
//                ErrorSection("Failed to load image")
//            }
        }

        "images" -> {
            // Handle plural "images" - array of image URLs
//            try {
                val imageUrls: List<String> = when (section.content) {
                    is List<*> -> {
                        (section.content as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    }
                    is String -> {
                        // Single URL as string, convert to list
                        listOf(section.content)
                    }
                    else -> {
                        // Try to parse as JSON array
                        try {
                            val contentStr = gson.toJson(section.content)
                            gson.fromJson(contentStr, object : TypeToken<List<String>>() {}.type)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }

                ImagesSection(urls = imageUrls)
//            } catch (e: Exception) {
//                Log.e("TileSectionRenderer", "Error parsing images section: ${e.message}")
//                ErrorSection("Failed to load images")
//            }
        }

        "table" -> {
            // Parse content to get table ID(s)
            val tableIds: List<String> = when (section.content) {
                is List<*> -> {
                    // Array of table IDs
                    (section.content as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                }
                is String -> {
                    // Single table ID
                    listOf(section.content)
                }
                else -> {
                    // Try to parse as JSON
                    try {
                        val contentStr = gson.toJson(section.content)
                        val parsed = gson.fromJson(contentStr, Any::class.java)
                        when (parsed) {
                            is List<*> -> parsed.mapNotNull { it?.toString() }
                            is String -> listOf(parsed)
                            else -> emptyList()
                        }
                    } catch (e: Exception) {
                        Log.e("TileSectionRenderer", "Error parsing table IDs: ${e.message}")
                        emptyList()
                    }
                }
            }

            // Look up and display each table
            tableIds.forEach { tableId ->
                val table = fields?.tableData?.find {it.id == tableId }
                if (table != null) {
                    TableCard(table = table)

                    // Add spacing between multiple tables
                    if (tableIds.size > 1 && tableId != tableIds.last()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    Log.w("TileSectionRenderer", "Table not found with ID: $tableId")
                }
            }
        }

        "link", "links" -> {
//            try {
                val contentStr = gson.toJson(section.content)
                val linksList: List<Map<String, String>> = when {
                    section.content is List<*> -> {
                        gson.fromJson(contentStr, object : TypeToken<List<Map<String, String>>>() {}.type)
                    }
                    else -> {
                        listOf(gson.fromJson(contentStr, object : TypeToken<Map<String, String>>() {}.type))
                    }
                }

                LinkSection(links = linksList, color = tileColor)
//            } catch (e: Exception) {
//                Log.e("TileSectionRenderer", "Error parsing link section: ${e.message}")
//                ErrorSection("Failed to load links")
//            }
        }

        "contact" -> {
//            try {
                val contentStr = gson.toJson(section.content)
                val contactObj = gson.fromJson(contentStr, Map::class.java)

                ContactSection(
                    phone = contactObj["phone"]?.toString(),
                    email = contactObj["email"]?.toString(),
                    color = tileColor
                )
//            } catch (e: Exception) {
//                Log.e("TileSectionRenderer", "Error parsing contact section: ${e.message}")
//                ErrorSection("Failed to load contact info")
//            }
        }

        else -> {
            Log.w("TileSectionRenderer", "Unknown section type: ${section.type}")
            PlaceholderSection(
                message = "Content type '${section.type}' is not yet supported",
                color = tileColor
            )
        }
    }
}

// Individual section composables

@Composable
private fun TextSection(content: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = content,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun ImageSection(url: String, caption: String? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            AsyncImage(
                model = url,
                contentDescription = caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    Log.d("ImagesSection", "Image loaded $url")
                },
                onError = {
                    Log.d("ImagesSection", "Failed to load image $url")
                }
            )
        }
    }
}

@Composable
private fun ImagesSection(urls: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        urls.forEach { url ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun LinkSection(links: List<Map<String, String>>, color: Color) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Links",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            links.forEachIndexed { index, link ->
                val label = link["label"] ?: "Link ${index + 1}"
                val url = link["url"] ?: link["link"] ?: ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            try {
                                val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                                    url
                                } else {
                                    "https://$url"
                                }
                                uriHandler.openUri(fullUrl)
                            } catch (e: Exception) {
                                Log.e("LinkSection", "Error opening URL: ${e.message}")
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = url,
                            fontSize = 13.sp,
                            color = color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = "Open",
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (index < links.size - 1) {
                    Divider(
                        modifier = Modifier.padding(start = 12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactSection(phone: String?, email: String?, color: Color) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Contact Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            phone?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val cleanedNumber = it.replace(Regex("[\\s-()]"), "")
                            val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$cleanedNumber")
                            }
                            context.startActivity(phoneIntent)
                        }
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Phone",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            email?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$it")
                            }
                            context.startActivity(emailIntent)
                        }
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Email",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderSection(message: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorSection(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// Keep existing helper composables for fallback

@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = 0.2f),
                                color.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ContactInfoCard(
    baseDetails: BaseDetails,
    fields: PageCards?
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Contact Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Phone
            if (fields?.show_phone == true && baseDetails.phone != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val cleanedNumber = baseDetails.phone.replace(Regex("[\\s-()]"), "")
                            val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$cleanedNumber")
                            }
                            context.startActivity(phoneIntent)
                        }
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Phone",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = baseDetails.phone,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Email
            if (fields?.show_email == true && baseDetails.email != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${baseDetails.email}")
                            }
                            context.startActivity(emailIntent)
                        }
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Email",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = baseDetails.email,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderCard(
    icon: ImageVector,
    title: String,
    message: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}