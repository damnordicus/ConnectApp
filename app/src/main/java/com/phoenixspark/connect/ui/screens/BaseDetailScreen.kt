package com.phoenixspark.connect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.phoenixspark.connect.Base
import com.phoenixspark.connect.SupabaseClient
import com.phoenixspark.connect.data.*
import com.phoenixspark.connect.data.repository.DataRepository
import com.phoenixspark.connect.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDetailScreen(
    base: Base,
    onBackPressed: () -> Unit = {},
    dataRepository: DataRepository? = null
) {
    var searchQueryName by remember { mutableStateOf("") }
    var organizations by remember { mutableStateOf<List<Organization>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf<OrganizationType?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedOrganization by remember { mutableStateOf<Organization?>(null) }
    var showModal by remember { mutableStateOf(false) }
    var baseDetails by remember { mutableStateOf<BaseDetails?>(null) }
    var selectedTab by remember { mutableStateOf("home") }
    var fields by remember { mutableStateOf<PageCards?>(null) }
    var showOrganizationDetail by remember { mutableStateOf(false) }
    var selectedTile by remember { mutableStateOf<BaseTile?>(null) }
    print("PageCards: $fields")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = dataRepository ?: remember{ DataRepository(context) }
    fun convertStringToOrganizationType(typeString: String): OrganizationType {
        return when (typeString.uppercase()) {
            "WING" -> OrganizationType.WING
            "GROUP" -> OrganizationType.GROUP
            "SQUADRON" -> OrganizationType.SQUADRON
            "AGENCY" -> OrganizationType.AGENCY
            "ORGANIZATION" -> OrganizationType.ORGANIZATION
            "SUPPORT" -> OrganizationType.SUPPORT
            else -> OrganizationType.ORGANIZATION
        }
    }

    fun loadOrganizations() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = SupabaseClient.getOrganizationsByBase(base.id)
                organizations = response.sortedBy { it.name }.map { orgResponse ->
                    Organization(
                        id = orgResponse.id,
                        name = orgResponse.name,
                        description = orgResponse.description,
                        contact = orgResponse.contact,
                        type = convertStringToOrganizationType(orgResponse.type),
                        baseId = orgResponse.base_id,
                        webUrl = orgResponse.web_url,
                        imageUrl = orgResponse.image_url,
                        primaryColor = orgResponse.primary_color,
                        secondaryColor = orgResponse.secondary_color,
                        textColor = orgResponse.text_color,
                        email = orgResponse.email,
                        building_number = orgResponse.building_number,
                        address = orgResponse.address,
                        links = orgResponse.links,
                        useTable = orgResponse.use_tables,
                        tableData = orgResponse.table_data,
                    )
                }

                val details = SupabaseClient.getBaseDetails(base.id)
                baseDetails = details.firstOrNull()?.let { detail ->
                    BaseDetails(
                        id = detail.id,
                        imageUrl = detail.image_url,
                        phone = detail.phone,
                        email = detail.email,
                        commander = detail.commander,
                        motto = detail.motto,
                        population = detail.population,
                        userId = detail.user_id,
                    )
                }

                val appFields = SupabaseClient.getAppFields(base.id)
                fields = appFields.firstOrNull()?.let { appfields ->
                    PageCards(
                        id = appfields.id,
                        base_id = appfields.base_id,
                        org_id = appfields.org_id,
                        show_name = appfields.show_name,
                        show_motto = appfields.show_motto,
                        show_commander = appfields.show_commander,
                        show_phone = appfields.show_phone,
                        show_email = appfields.show_email,
                        show_tables = appfields.show_tables,
                        tableData = appfields.table_data,
                        tilesConfig = appfields.tiles_config,
                    )
                }
            } catch (e: Exception) {
                println("⚠️ Network failed: ${e.message}, trying cache...")
                try {
                    // Load from cache
                    val orgResponse = repository.getOrganizationsByBaseSync(base.id)
                    organizations = orgResponse.sortedBy { it.name }.map { orgResponse ->
                        Organization(
                            id = orgResponse.id,
                            name = orgResponse.name,
                            description = orgResponse.description,
                            contact = orgResponse.contact,
                            type = convertStringToOrganizationType(orgResponse.type),
                            baseId = orgResponse.base_id,
                            webUrl = orgResponse.web_url,
                            imageUrl = orgResponse.image_url,
                            primaryColor = orgResponse.primary_color,
                            secondaryColor = orgResponse.secondary_color,
                            textColor = orgResponse.text_color,
                            email = orgResponse.email,
                            building_number = orgResponse.building_number,
                            address = orgResponse.address,
                            links = orgResponse.links,
                            useTable = orgResponse.use_tables,
                            tableData = orgResponse.table_data,
                        )
                    }

                    baseDetails = repository.getBaseDetails(base.id)?.let { detail ->
                        BaseDetails(
                            id = detail.id,
                            imageUrl = detail.image_url,
                            phone = detail.phone,
                            email = detail.email,
                            commander = detail.commander,
                            motto = detail.motto,
                            population = detail.population,
                            userId = detail.user_id,
                        )
                    }
                    fields = repository.getPageCards(base.id)?.let { appfields ->
                        PageCards(
                            id = appfields.id,
                            base_id = appfields.base_id,
                            org_id = appfields.org_id,
                            show_name = appfields.show_name,
                            show_motto = appfields.show_motto,
                            show_commander = appfields.show_commander,
                            show_phone = appfields.show_phone,
                            show_email = appfields.show_email,
                            show_tables = appfields.show_tables,
                            tableData = appfields.table_data,
                            tilesConfig = appfields.tiles_config
                        )
                    }

                    println("✅ Loaded ${organizations.size} orgs from cache")
                } catch (cacheError: Exception) {
                    errorMessage = "Failed to load: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOrganizations()
    }

    // Show organization detail screen if selected
    if (showOrganizationDetail && selectedOrganization != null) {
        OrganizationDetailScreen(
            organization = selectedOrganization!!,
            onBackPressed = {
                showOrganizationDetail = false
                selectedOrganization = null
            }
        )
        return
    }

    // Show tile detail screen if selected
    if (selectedTile != null && baseDetails != null) {
        TileDetailScreen(
            tile = selectedTile!!,
            baseDetails = baseDetails!!,
            fields = fields,
            onBackPressed = { selectedTile = null }
        )
        return
    }

    val filteredOrganizations = organizations
        .filter { org ->
            val matchesSearch = org.name.contains(searchQueryName, ignoreCase = true) ||
                    org.description?.contains(searchQueryName, ignoreCase = true) == true ||
                    org.building_number?.contains(searchQueryName, ignoreCase = true) == true
            val matchesFilter = selectedFilter == null || org.type == selectedFilter
            matchesSearch && matchesFilter
        }
        .sortedBy { it.name }

    val organizationCounts = organizations.groupBy { it.type }.mapValues { it.value.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFF1F5F9)
                    )
                )
            )
    ) {
        // Sticky Top Navigation Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .shadow(4.dp),
            shape = AbsoluteCutCornerShape(0.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
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
                    text = base.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { loadOrganizations() },
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = if (isLoading) Color.Gray else MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            // Base Information Card with Image
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        if (baseDetails?.imageUrl != null) {
                            AsyncImage(
                                model = baseDetails?.imageUrl,
                                contentDescription = "${base.name} background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (fields?.show_name == true) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF6366F1).copy(alpha = 0.8f),
                                                Color(0xFF3B82F6).copy(alpha = 0.9f)
                                            )
                                        )
                                    )
                            )
                        }
                        if (fields?.show_name != false) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = base.name,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "${base.city}, ${base.state}",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Tab Toggle Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { selectedTab = "home" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == "home")
                                    MaterialTheme.colorScheme.tertiary.copy(0.6f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selectedTab == "home")
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Home")
                        }

                        Button(
                            onClick = { selectedTab = "organizations" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == "organizations")
                                    MaterialTheme.colorScheme.tertiary.copy(0.6f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selectedTab == "organizations")
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = "Organizations",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Organizations")
                        }
                    }
                }
            }

            // HOME TAB - Tile Grid
            if (selectedTab == "home") {
                item {
                    TileGrid(
                        tiles = getDefaultBaseTiles(fields?.tilesConfig ?: emptyList()),
                        onTileClick = { tile ->
                            selectedTile = tile
                        }
                    )
                }
            }

            // ORGANIZATIONS TAB - (keep your existing organization list code)
            if (selectedTab == "organizations") {
                // ... (keep all your existing organization search/filter/list code)
                // Search Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp, 8.dp)
                        ) {
                            val focusManager = LocalFocusManager.current
                            OutlinedTextField(
                                value = searchQueryName,
                                onValueChange = { searchQueryName = it },
                                placeholder = { Text("Search by name or building number") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp, 4.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                    }
                                )
                            )

                            Text(
                                text = if (isLoading) "Loading..." else "${filteredOrganizations.size} organizations found",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 8.dp, start = 8.dp)
                            )

                            errorMessage?.let { error ->
                                Text(
                                    text = error,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Filter Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Filter by Type",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    FilterChip(
                                        onClick = { selectedFilter = null },
                                        label = { Text("All") },
                                        selected = selectedFilter == null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF6366F1),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }

                                items(OrganizationType.values().toList()) { type ->
                                    val count = organizationCounts[type] ?: 0
                                    if (count > 0) {
                                        FilterChip(
                                            onClick = {
                                                selectedFilter = if (selectedFilter == type) null else type
                                            },
                                            label = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = type.icon,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text("${type.displayName} ($count)")
                                                }
                                            },
                                            selected = selectedFilter == type,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = type.color,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (isLoading && organizations.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF6366F1)
                            )
                        }
                    }
                } else {
                    items(filteredOrganizations) { organization ->
                        OrganizationCard(
                            organization = organization,
                            onClick = {
                                selectedOrganization = organization
                                showModal = true
                            }
                        )
                    }

                    if (filteredOrganizations.isEmpty() && !isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No organizations found",
                                    fontSize = 16.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showModal && selectedOrganization != null) {
            OrganizationDetailModal(
                organization = selectedOrganization!!,
                onDismiss = {
                    showModal = false
                    selectedOrganization = null
                },
                onViewDetails = {
                    showModal = false
                    showOrganizationDetail = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDetailScreenWithOffline(
    base: Base,
    onBackPressed: () -> Unit = {},
    isOnline: Boolean,  // Changed from NetworkMonitor
    dataRepository: DataRepository,
) {
    var searchQueryName by remember { mutableStateOf("") }
    var organizations by remember { mutableStateOf<List<Organization>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf<OrganizationType?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedOrganization by remember { mutableStateOf<Organization?>(null) }
    var showModal by remember { mutableStateOf(false) }
    var baseDetails by remember { mutableStateOf<BaseDetails?>(null) }
    var selectedTab by remember { mutableStateOf("home") }
    var fields by remember { mutableStateOf<PageCards?>(null) }
    var showOrganizationDetail by remember { mutableStateOf(false) }
    var selectedTile by remember { mutableStateOf<BaseTile?>(null) }

    val scope = rememberCoroutineScope()

    fun convertStringToOrganizationType(typeString: String): OrganizationType {
        return when (typeString.uppercase()) {
            "WING" -> OrganizationType.WING
            "GROUP" -> OrganizationType.GROUP
            "SQUADRON" -> OrganizationType.SQUADRON
            "AGENCY" -> OrganizationType.AGENCY
            "ORGANIZATION" -> OrganizationType.ORGANIZATION
            "SUPPORT" -> OrganizationType.SUPPORT
            else -> OrganizationType.ORGANIZATION
        }
    }

    fun loadOrganizations() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                if (isOnline) {
                    // Try to fetch from network
                    val response = SupabaseClient.getOrganizationsByBase(base.id)
                    organizations = response.sortedBy { it.name }.map { orgResponse ->
                        Organization(
                            id = orgResponse.id,
                            name = orgResponse.name,
                            description = orgResponse.description,
                            contact = orgResponse.contact,
                            type = convertStringToOrganizationType(orgResponse.type),
                            baseId = orgResponse.base_id,
                            webUrl = orgResponse.web_url,
                            imageUrl = orgResponse.image_url,
                            primaryColor = orgResponse.primary_color,
                            secondaryColor = orgResponse.secondary_color,
                            textColor = orgResponse.text_color,
                            email = orgResponse.email,
                            building_number = orgResponse.building_number,
                            address = orgResponse.address,
                            links = orgResponse.links,
                            useTable = orgResponse.use_tables,
                            tableData = orgResponse.table_data,
                        )
                    }

                    val details = SupabaseClient.getBaseDetails(base.id)
                    baseDetails = details.firstOrNull()?.let { detail ->
                        BaseDetails(
                            id = detail.id,
                            imageUrl = detail.image_url,
                            phone = detail.phone,
                            email = detail.email,
                            commander = detail.commander,
                            motto = detail.motto,
                            population = detail.population,
                            userId = detail.user_id,
                        )
                    }

                    val appFields = SupabaseClient.getAppFields(base.id)
                    fields = appFields.firstOrNull()?.let { appfields ->
                        PageCards(
                            id = appfields.id,
                            base_id = appfields.base_id,
                            org_id = appfields.org_id,
                            show_name = appfields.show_name,
                            show_motto = appfields.show_motto,
                            show_commander = appfields.show_commander,
                            show_phone = appfields.show_phone,
                            show_email = appfields.show_email,
                            show_tables = appfields.show_tables,
                            tableData = appfields.table_data,
                            tilesConfig = appfields.tiles_config,
                        )
                    }
                } else {
                    // Load from local database
                    val orgResponse = dataRepository.getOrganizationsByBaseSync(base.id)
                    organizations = orgResponse.sortedBy { it.name }.map { orgResponse ->
                        Organization(
                            id = orgResponse.id,
                            name = orgResponse.name,
                            description = orgResponse.description,
                            contact = orgResponse.contact,
                            type = convertStringToOrganizationType(orgResponse.type),
                            baseId = orgResponse.base_id,
                            webUrl = orgResponse.web_url,
                            imageUrl = orgResponse.image_url,
                            primaryColor = orgResponse.primary_color,
                            secondaryColor = orgResponse.secondary_color,
                            textColor = orgResponse.text_color,
                            email = orgResponse.email,
                            building_number = orgResponse.building_number,
                            address = orgResponse.address,
                            links = orgResponse.links,
                            useTable = orgResponse.use_tables,
                            tableData = orgResponse.table_data,
                        )
                    }

                    val detailResponse = dataRepository.getBaseDetails(base.id)
                    baseDetails = detailResponse?.let { detail ->
                        BaseDetails(
                            id = detail.id,
                            imageUrl = detail.image_url,
                            phone = detail.phone,
                            email = detail.email,
                            commander = detail.commander,
                            motto = detail.motto,
                            population = detail.population,
                            userId = detail.user_id,
                        )
                    }

                    val fieldsResponse = dataRepository.getPageCards(base.id)
                    fields = fieldsResponse?.let { appfields ->
                        PageCards(
                            id = appfields.id,
                            base_id = appfields.base_id,
                            org_id = appfields.org_id,
                            show_name = appfields.show_name,
                            show_motto = appfields.show_motto,
                            show_commander = appfields.show_commander,
                            show_phone = appfields.show_phone,
                            show_email = appfields.show_email,
                            show_tables = appfields.show_tables,
                            tableData = appfields.table_data,
                            tilesConfig = appfields.tiles_config,
                        )
                    }

                    if (organizations.isEmpty()) {
                        errorMessage = "No cached data available. Connect to internet and refresh."
                    }
                }
            } catch (e: Exception) {
                println("Error loading data: ${e.message}")
                errorMessage = if (isOnline) {
                    "Failed to load from server: ${e.message}"
                } else {
                    "No cached data available"
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOrganizations()
    }

    // Rest of the screen implementation stays the same...
    // (Keep your existing UI code for showing organizations, tiles, etc.)

    // Show organization detail screen if selected
    if (showOrganizationDetail && selectedOrganization != null) {
        OrganizationDetailScreen(
            organization = selectedOrganization!!,
            onBackPressed = {
                showOrganizationDetail = false
                selectedOrganization = null
            }
        )
        return
    }

    // Show tile detail screen if selected
    if (selectedTile != null && baseDetails != null) {
        TileDetailScreen(
            tile = selectedTile!!,
            baseDetails = baseDetails!!,
            fields = fields,
            onBackPressed = { selectedTile = null }
        )
        return
    }
    BaseDetailScreen(
        base = base,
        onBackPressed = onBackPressed,
    )
    // Continue with your existing BaseDetailScreen UI code...
    // (Use the same UI from your current BaseDetailScreen but with the offline-capable loadOrganizations)
}
