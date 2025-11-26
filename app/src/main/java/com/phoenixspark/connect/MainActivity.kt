package com.phoenixspark.connect

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.google.gson.reflect.TypeToken
import com.phoenixspark.connect.ui.screens.BaseDetailScreen
import com.phoenixspark.connect.ui.theme.ConnectTheme

// Updated Base data class to match Supabase response
@Serializable
data class Base(
    val id: String,
    val name: String,
    val city: String,
    val state: String,
    val active: Boolean? = null,
    val type: BaseType = BaseType.BUSINESS
)

enum class BaseType(val icon: ImageVector, val color: Color){
    BUSINESS(Icons.Default.Business, Color(0xFF10B981))
}

// Storage manager for saved bases
class BaseStorageManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("saved_bases", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveBases(bases: List<Base>) {
        val json = gson.toJson(bases)
        prefs.edit().putString("bases", json).apply()
    }

    fun getSavedBases(): List<Base> {
        val json = prefs.getString("bases", null) ?: return emptyList()
        val type = object : TypeToken<List<Base>>() {}.type
        return gson.fromJson(json, type)
    }

    fun hasSavedBases(): Boolean {
        return getSavedBases().isNotEmpty()
    }

    fun clearSavedBases() {
        prefs.edit().remove("bases").apply()
    }
}

// App modes
enum class AppMode {
    SELECTION,      // Initial base selection mode
    SAVED_BASES,    // Show saved bases
    BASE_DETAIL     // Show detailed view of a base
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            ConnectTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }
}

@Composable
fun AppNavigation(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    val storageManager = remember { BaseStorageManager(context) }
    var selectedBase by remember { mutableStateOf<Base?>(null) }
    var appMode by remember { mutableStateOf(AppMode.SELECTION) }
    var savedBases by remember { mutableStateOf<List<Base>>(emptyList()) }
//    var isDarkTheme by remember { mutableStateOf(true) }

    // Check for saved bases on startup
    LaunchedEffect(Unit) {
        savedBases = storageManager.getSavedBases()
        if (savedBases.isNotEmpty()) {
            appMode = AppMode.SAVED_BASES
        }
    }

    when (appMode) {
        AppMode.SELECTION -> {
            DirectoryScreen(
                isSelectionMode = true,
                preSelectedBases = savedBases, // Pass saved bases for editing
                onBaseClick = { /* Not used in selection mode */ },
                onBasesSelected = { selectedBasesList ->
                    storageManager.saveBases(selectedBasesList)
                    savedBases = selectedBasesList
                    appMode = AppMode.SAVED_BASES
                }
            )
        }

        AppMode.SAVED_BASES -> {
            SavedBasesScreen(
                savedBases = savedBases.sortedBy { it.name },
                onBaseSelected = { base ->
                    selectedBase = base
                    appMode = AppMode.BASE_DETAIL
                },
                onManageBases = {
                    appMode = AppMode.SELECTION
                },
                onClearBases = {
                    storageManager.clearSavedBases()
                    savedBases = emptyList()
                    appMode = AppMode.SELECTION
                },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        AppMode.BASE_DETAIL -> {
            selectedBase?.let { base ->
                BaseDetailScreen(
                    base = base,
                    onBackPressed = {
                        selectedBase = null
                        appMode = AppMode.SAVED_BASES
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    isSelectionMode: Boolean = false,
    onBaseClick: (Base) -> Unit = {},
    onBasesSelected: (List<Base>) -> Unit = {},
    preSelectedBases: List<Base> = emptyList()
) {
    var searchQuery by remember { mutableStateOf("") }
    var bases by remember { mutableStateOf<List<Base>>(emptyList()) }
    var selectedBases by remember { mutableStateOf<Set<Base>>(preSelectedBases.toSet()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Function to load bases from Supabase
    fun loadBases() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = SupabaseClient.getBases()
                bases = response.map { baseResponse ->
                    Base(
                        id = baseResponse.id,
                        name = baseResponse.name,
                        city = baseResponse.city,
                        state = baseResponse.state,
                        active = baseResponse.active
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load bases: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Load bases when the screen first appears
    LaunchedEffect(Unit) {
        loadBases()
    }

    val filteredItems = bases.sortedBy{ it.name }.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.city.contains(searchQuery, ignoreCase = true) ||
                it.state.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        // Header with gradient background
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary.copy(0.4f))
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Virtual Directory",
                                textAlign = TextAlign.Center,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = if (isSelectionMode) {
                                    if (selectedBases.isEmpty()) "Choose bases to follow" else "${selectedBases.size} bases selected"
                                } else {
                                    if (isLoading) "Loading..." else "${filteredItems.size} entries found"
                                },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Refresh button
                        IconButton(
                            onClick = { loadBases() },
                            enabled = !isLoading
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = if (isLoading) Color.Gray else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search bases...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF64748B)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )

                    // Selection mode controls
                    if (isSelectionMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedBases.size} bases selected",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(
                                    onClick = { selectedBases = emptySet() }
                                ) {
                                    Text(
                                        text = "Clear All",
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }

                                Button(
                                    onClick = { onBasesSelected(selectedBases.toList()) },
                                    enabled = selectedBases.isNotEmpty(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text(
                                        text = "Save (${selectedBases.size})",
                                        color = if (selectedBases.size > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                                            0.2f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Error message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator or directory list
        if (isLoading && bases.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF6366F1)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { item ->
                    BaseCard(
                        base = item,
                        isSelectionMode = isSelectionMode,
                        isSelected = selectedBases.contains(item),
                        onClick = {
                            if (isSelectionMode) {
                                selectedBases = if (selectedBases.contains(item)) {
                                    selectedBases - item
                                } else {
                                    selectedBases + item
                                }
                            } else {
                                onBaseClick(item)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BaseCard(
    base: Base,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .border(
                width = if (isSelected) 2.dp else 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiary.copy(0.4f),
                shape = RoundedCornerShape(16.dp),
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection mode
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.tertiary
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = base.type.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = base.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${base.city}, ${base.state}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )

//                Row(
//                    modifier = Modifier.padding(top = 8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    // Status badge
//                    Surface(
//                        shape = RoundedCornerShape(8.dp),
//                        color = if (base.active == true) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
//                        modifier = Modifier
//                    ) {
//                        Text(
//                            text = if (base.active == true) "Active" else "Status Unknown",
//                            fontSize = 12.sp,
//                            color = if (base.active == true) Color(0xFF166534) else Color(0xFF475569),
//                            fontWeight = FontWeight.Medium,
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        )
//                    }
//                }
            }

            // Selection indicator or status indicator
//            if (isSelectionMode && isSelected) {
//                Icon(
//                    Icons.Default.CheckCircle,
//                    contentDescription = "Selected",
//                    tint = Color(0xFF5BA3FF),
//                    modifier = Modifier.size(24.dp)
//                )
//            } else if (!isSelectionMode) {
//                Box(
//                    modifier = Modifier
//                        .size(8.dp)
//                        .clip(CircleShape)
//                        .background(
//                            if (base.active == true) Color(0xFF10B981) else Color(0xFF94A3B8)
//                        )
//                )
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedBasesScreen(
    savedBases: List<Base>,
    onBaseSelected: (Base) -> Unit,
    onManageBases: () -> Unit,
    onClearBases: () -> Unit,
    isDarkTheme: Boolean = true, // Add this parameter
    onThemeChange: (Boolean) -> Unit = {} // Add this parameter
) {
    var showMenu by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Virtual Directory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${savedBases.size} bases saved",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onManageBases()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onClearBases()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.RestoreFromTrash,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("App Settings") },
                            onClick = {
                                showMenu = false
                                showSettingsDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        )
                    }
                }
            }
        }

        // Bases Grid
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(savedBases) { base ->
                SavedBaseCard(
                    base = base,
                    onClick = { onBaseSelected(base) }
                )
            }
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "App Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showSettingsDialog = false }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dark Theme",
                            fontSize = 16.sp
                        )
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = onThemeChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Designed By Adam Nord @ Travis AFB Phoenix Spark 2025",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(16.dp)
        )
    }
}
@Composable
fun SavedBaseCard(
    base: Base,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),  // Move height here
            contentAlignment = Alignment.Center  // <-- Add this to center content vertically
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()  // Changed from fillMaxWidth
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            // Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),  // Only horizontal padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = base.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${base.city}, ${base.state}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
@Composable
fun VirtualDirectoryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6366F1),
            secondary = Color(0xFF8B5CF6),
            background = Color(0xFFF8FAFC)
        ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DirectoryScreenPreview() {
    VirtualDirectoryTheme {
        DirectoryScreen()
    }
}