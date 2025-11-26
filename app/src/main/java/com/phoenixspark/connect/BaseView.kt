package com.phoenixspark.connect

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.phoenixspark.connect.data.Organization
import com.phoenixspark.connect.ui.screens.BaseDetailScreen


//data class TableData(
//    val id: String,
//    val headers: List<String>,
//    val data: List<List<String>>
//)

// Data classes for organizations
//data class Organization(
//    val id: String,
//    val name: String,
//    val type: OrganizationType,
//    val description: String?,
//    val contact: String?,
//    val baseId: String,
//    val webUrl: String? = null,
//    val primaryColor: String? = null,
//    val secondaryColor: String? = null,
//    val textColor: String? = null,
//    val imageUrl: String? = null,
//    val email: String,
//    val building_number: String? = null,
//    val address: String? = null,
//    val links: String? = null,
//    val useTable: Boolean,
//    val tableData: List<TableData> = emptyList(),
//)

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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BaseDetailScreen(
//    base: Base,
//    onBackPressed: () -> Unit = {}
//) {
//    var searchQueryName by remember { mutableStateOf("") }
//    var organizations by remember { mutableStateOf<List<Organization>>(emptyList()) }
//    var selectedFilter by remember { mutableStateOf<OrganizationType?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    var selectedOrganization by remember { mutableStateOf<Organization?>(null) }
//    var showModal by remember { mutableStateOf(false) }
//    var baseDetails by remember { mutableStateOf<BaseDetails?>(null)}
//    var selectedTab by remember { mutableStateOf("organizations") }
//    var selectedSearch by remember { mutableStateOf("name")}
//    var fields by remember {mutableStateOf<PageCards?>(null)}
//    var showOrganizationDetail by remember { mutableStateOf(false) }
//
//    val scope = rememberCoroutineScope()
//
//    fun convertStringToOrganizationType(typeString: String): OrganizationType {
//        return when (typeString.uppercase()) {
//            "WING" -> OrganizationType.WING
//            "GROUP" -> OrganizationType.GROUP
//            "SQUADRON" -> OrganizationType.SQUADRON
//            "AGENCY" -> OrganizationType.AGENCY
//            "ORGANIZATION" -> OrganizationType.ORGANIZATION
//            "SUPPORT" -> OrganizationType.SUPPORT
//            else -> OrganizationType.ORGANIZATION
//        }
//    }
//
//    fun loadOrganizations() {
//        println(base.id)
//        scope.launch {
//            isLoading = true
//            errorMessage = null
//            try {
//                val response = SupabaseClient.getOrganizationsByBase(base.id)
//                organizations = response.sortedBy { it.name }.map { orgResponse ->
//                    Organization(
//                        id = orgResponse.id,
//                        name = orgResponse.name,
//                        description = orgResponse.description,
//                        contact = orgResponse.contact,
//                        type = convertStringToOrganizationType(orgResponse.type),
//                        baseId = orgResponse.base_id,
//                        webUrl = orgResponse.web_url,
//                        imageUrl = orgResponse.image_url,
//                        primaryColor = orgResponse.primary_color,
//                        secondaryColor = orgResponse.secondary_color,
//                        textColor = orgResponse.text_color,
//                        email = orgResponse.email,
//                        building_number = orgResponse.building_number,
//                        address = orgResponse.address,
//                        links = orgResponse.links,
//                        useTable = orgResponse.use_tables,
//                        tableData = orgResponse.table_data,
//                    )
//                }
//                val details = SupabaseClient.getBaseDetails(base.id)
//                baseDetails = details.firstOrNull()?.let { detail ->
//                    BaseDetails(
//                        id = detail.id,
//                        imageUrl = detail.image_url,
//                        phone = detail.phone,
//                        email = detail.email,
//                        commander = detail.commander,
//                        motto = detail.motto,
//                        population = detail.population,
//                        userId = detail.user_id,
//                    )
//                }
//                println("test")
//                val appFields = SupabaseClient.getAppFields(base.id)
//                fields = appFields.firstOrNull()?.let {appfields ->
//                    PageCards(
//                        id = appfields.id,
//                        base_id = appfields.base_id,
//                        org_id = appfields.org_id,
//                        show_name = appfields.show_name,
//                        show_motto = appfields.show_motto,
//                        show_commander = appfields.show_commander,
//                        show_phone = appfields.show_phone,
//                        show_email = appfields.show_email,
//                        show_tables = appfields.show_tables,
//                        tableData = appfields.table_data,
//                    )
//                }
//            } catch (e: Exception) {
//                errorMessage = "Failed to load organizations: ${e.message}"
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        loadOrganizations()
//    }
//
//    // Show organization detail screen if selected
//    if (showOrganizationDetail && selectedOrganization != null) {
//        OrganizationDetailScreen(
//            organization = selectedOrganization!!,
//            onBackPressed = {
//                showOrganizationDetail = false
//                selectedOrganization = null
//            }
//        )
//        return
//    }
//
//    val filteredOrganizations = organizations
//        .filter { org ->
//            val matchesSearch = when (selectedSearch) {
//                "name" -> org.name.contains(searchQueryName, ignoreCase = true) ||
//                        org.description?.contains(searchQueryName, ignoreCase = true) == true ||
//                        org.building_number?.contains(searchQueryName, ignoreCase = true) == true
//                else -> true
//            }
//            val matchesFilter = selectedFilter == null || org.type == selectedFilter
//            matchesSearch && matchesFilter
//        }
//        .sortedBy { it.name }
//
//    val organizationCounts = organizations.groupBy { it.type }.mapValues { it.value.size }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFF8FAFC),
//                        Color(0xFFF1F5F9)
//                    )
//                )
//            )
//    ) {
//        // Sticky Top Navigation Bar
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .zIndex(1f)
//                .shadow(4.dp),
//            shape = AbsoluteCutCornerShape(0.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(
//                    onClick = onBackPressed
//                ) {
//                    Icon(
//                        Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = Color(0xFF6366F1)
//                    )
//                }
//
//                Text(
//                    text = base.name,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF1E293B),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.weight(1f)
//                )
//
//                IconButton(
//                    onClick = { loadOrganizations() },
//                    enabled = !isLoading
//                ) {
//                    Icon(
//                        Icons.Default.Refresh,
//                        contentDescription = "Refresh",
//                        tint = if (isLoading) Color.Gray else Color(0xFF6366F1)
//                    )
//                }
//            }
//        }
//
//        // Scrollable Content
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 80.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//            contentPadding = PaddingValues(8.dp)
//        ) {
//            // Base Information Card with Image
//            item {
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .shadow(8.dp, RoundedCornerShape(16.dp)),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color.White)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                    ) {
//                        if (baseDetails?.imageUrl != null) {
//                            AsyncImage(
//                                model = baseDetails?.imageUrl,
//                                contentDescription = "${base.name} background",
//                                modifier = Modifier.fillMaxSize(),
//                                contentScale = ContentScale.Crop
//                            )
//                            if(fields?.show_name == true){
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .background(Color.Black.copy(alpha = 0.4f))
//                                )
//                            }
//                        } else {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(
//                                        brush = Brush.verticalGradient(
//                                            colors = listOf(
//                                                Color(0xFF6366F1).copy(alpha = 0.8f),
//                                                Color(0xFF3B82F6).copy(alpha = 0.9f)
//                                            )
//                                        )
//                                    )
//                            )
//                        }
//                        if(fields?.show_name != false) {
//                            Column(
//                                modifier = Modifier
//                                    .align(Alignment.Center)
//                                    .padding(20.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = base.name,
//                                    fontSize = 24.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.White,
//                                    maxLines = 2,
//                                    overflow = TextOverflow.Ellipsis,
//                                    textAlign = TextAlign.Center
//                                )
//
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Text(
//                                    text = "${base.city}, ${base.state}",
//                                    fontSize = 16.sp,
//                                    color = Color.White.copy(alpha = 0.9f),
//                                    fontWeight = FontWeight.Medium,
//                                    textAlign = TextAlign.Center
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            // Tab Toggle Card
//            item {
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .shadow(8.dp, RoundedCornerShape(16.dp)),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color.White)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Button(
//                            onClick = { selectedTab = "organizations" },
//                            modifier = Modifier.weight(1f),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = if (selectedTab == "organizations")
//                                    Color(0xFF6366F1)
//                                else
//                                    Color(0xFFF1F5F9),
//                                contentColor = if (selectedTab == "organizations")
//                                    Color.White
//                                else
//                                    Color(0xFF64748B)
//                            ),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Icon(
//                                Icons.Default.Groups,
//                                contentDescription = "Organizations",
//                                modifier = Modifier.size(18.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Organizations")
//                        }
//
//                        Button(
//                            onClick = { selectedTab = "baseDetails" },
//                            modifier = Modifier.weight(1f),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = if (selectedTab == "baseDetails")
//                                    Color(0xFF6366F1)
//                                else
//                                    Color(0xFFF1F5F9),
//                                contentColor = if (selectedTab == "baseDetails")
//                                    Color.White
//                                else
//                                    Color(0xFF64748B)
//                            ),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Icon(
//                                Icons.Default.Info,
//                                contentDescription = "Base Details",
//                                modifier = Modifier.size(18.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Base Details")
//                        }
//                    }
//                }
//            }
//
//            // Conditional content based on selected tab
//            if (selectedTab == "organizations") {
//                // Search Card
//                item {
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .shadow(8.dp, RoundedCornerShape(16.dp)),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(8.dp, 8.dp)
//                        ) {
//                            val focusManager = LocalFocusManager.current
//                            OutlinedTextField(
//                                value = searchQueryName,
//                                onValueChange = { searchQueryName = it },
//                                placeholder = { Text("Search by name or building number") },
//                                leadingIcon = {
//                                    Icon(
//                                        Icons.Default.Search,
//                                        contentDescription = "Search",
//                                        tint = Color(0xFF64748B)
//                                    )
//                                },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(4.dp, 4.dp)
//                                    .clip(RoundedCornerShape(12.dp)),
//                                shape = RoundedCornerShape(12.dp),
//                                colors = OutlinedTextFieldDefaults.colors(
//                                    focusedBorderColor = Color(0xFF6366F1),
//                                    unfocusedBorderColor = Color(0xFFE2E8F0),
//                                    focusedContainerColor = Color.White,
//                                    unfocusedContainerColor = Color(0xFFFAFAFA)
//                                ),
//                                singleLine = true,
//                                keyboardOptions = KeyboardOptions(
//                                    imeAction = ImeAction.Search
//                                ),
//                                keyboardActions = KeyboardActions(
//                                    onSearch = {
//                                        focusManager.clearFocus()
//                                    }
//                                )
//                            )
//
//                            Text(
//                                text = if (isLoading) "Loading..." else "${filteredOrganizations.size} organizations found",
//                                fontSize = 14.sp,
//                                color = Color(0xFF64748B),
//                                modifier = Modifier.padding(top = 8.dp, start = 8.dp)
//                            )
//
//                            errorMessage?.let { error ->
//                                Text(
//                                    text = error,
//                                    color = Color.Red,
//                                    fontSize = 12.sp,
//                                    modifier = Modifier.padding(top = 4.dp)
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // Filter Card
//                item {
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .shadow(4.dp, RoundedCornerShape(12.dp)),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(16.dp)
//                        ) {
//                            Text(
//                                text = "Filter by Type",
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.SemiBold,
//                                color = Color(0xFF1E293B),
//                                modifier = Modifier.padding(bottom = 8.dp)
//                            )
//
//                            LazyRow(
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                item {
//                                    FilterChip(
//                                        onClick = { selectedFilter = null },
//                                        label = { Text("All") },
//                                        selected = selectedFilter == null,
//                                        colors = FilterChipDefaults.filterChipColors(
//                                            selectedContainerColor = Color(0xFF6366F1),
//                                            selectedLabelColor = Color.White
//                                        )
//                                    )
//                                }
//
//                                items(OrganizationType.values().toList()) { type ->
//                                    val count = organizationCounts[type] ?: 0
//                                    if (count > 0) {
//                                        FilterChip(
//                                            onClick = {
//                                                selectedFilter = if (selectedFilter == type) null else type
//                                            },
//                                            label = {
//                                                Row(
//                                                    verticalAlignment = Alignment.CenterVertically,
//                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                                                ) {
//                                                    Icon(
//                                                        imageVector = type.icon,
//                                                        contentDescription = null,
//                                                        modifier = Modifier.size(16.dp)
//                                                    )
//                                                    Text("${type.displayName} ($count)")
//                                                }
//                                            },
//                                            selected = selectedFilter == type,
//                                            colors = FilterChipDefaults.filterChipColors(
//                                                selectedContainerColor = type.color,
//                                                selectedLabelColor = Color.White
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // Organizations list
//                if (isLoading && organizations.isEmpty()) {
//                    item {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator(
//                                color = Color(0xFF6366F1)
//                            )
//                        }
//                    }
//                } else {
//                    items(filteredOrganizations) { organization ->
//                        OrganizationCard(
//                            organization = organization,
//                            onClick = {
//                                selectedOrganization = organization
//                                showModal = true
//                            }
//                        )
//                    }
//
//                    if (filteredOrganizations.isEmpty() && !isLoading) {
//                        item {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(32.dp),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    text = "No organizations found",
//                                    fontSize = 16.sp,
//                                    color = Color(0xFF64748B)
//                                )
//                            }
//                        }
//                    }
//                }
//            } else {
//                // Base Details View
//                baseDetails?.let { details ->
//                    // Commander Card
//                    if(fields?.show_commander == true) {
//
//
//                        details.commander?.let { commander ->
//                            item {
//                                Card(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .shadow(4.dp, RoundedCornerShape(16.dp)),
//                                    shape = RoundedCornerShape(16.dp),
//                                    colors = CardDefaults.cardColors(containerColor = Color.White)
//                                ) {
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(20.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Box(
//                                            modifier = Modifier
//                                                .size(56.dp)
//                                                .clip(CircleShape)
//                                                .background(
//                                                    brush = Brush.radialGradient(
//                                                        colors = listOf(
//                                                            Color(0xFF6366F1).copy(alpha = 0.2f),
//                                                            Color(0xFF6366F1).copy(alpha = 0.1f)
//                                                        )
//                                                    )
//                                                ),
//                                            contentAlignment = Alignment.Center
//                                        ) {
//                                            Icon(
//                                                Icons.Default.Person,
//                                                contentDescription = "Commander",
//                                                tint = Color(0xFF6366F1),
//                                                modifier = Modifier.size(28.dp)
//                                            )
//                                        }
//
//                                        Spacer(modifier = Modifier.width(16.dp))
//
//                                        Column {
//                                            Text(
//                                                text = "Commander",
//                                                fontSize = 14.sp,
//                                                color = Color(0xFF64748B),
//                                                fontWeight = FontWeight.Medium
//                                            )
//                                            Text(
//                                                text = commander,
//                                                fontSize = 18.sp,
//                                                fontWeight = FontWeight.SemiBold,
//                                                color = Color(0xFF1E293B)
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    // Motto Card
//                    println("${fields}")
//                    if(fields?.show_motto == true) {
//
//
//                        details.motto?.let { motto ->
//                            item {
//                                Card(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .shadow(4.dp, RoundedCornerShape(16.dp)),
//                                    shape = RoundedCornerShape(16.dp),
//                                    colors = CardDefaults.cardColors(
//                                        containerColor = Color(
//                                            0xFFF0F9FF
//                                        )
//                                    )
//                                ) {
//                                    Column(
//                                        modifier = Modifier.padding(20.dp)
//                                    ) {
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            modifier = Modifier.padding(bottom = 8.dp)
//                                        ) {
//                                            Icon(
//                                                Icons.Default.FormatQuote,
//                                                contentDescription = "Motto",
//                                                tint = Color(0xFF6366F1),
//                                                modifier = Modifier.size(24.dp)
//                                            )
//                                            Spacer(modifier = Modifier.width(8.dp))
//                                            Text(
//                                                text = "Base Motto",
//                                                fontSize = 16.sp,
//                                                fontWeight = FontWeight.SemiBold,
//                                                color = Color(0xFF1E293B)
//                                            )
//                                        }
//                                        Text(
//                                            text = "\"$motto\"",
//                                            fontSize = 16.sp,
//                                            color = Color(0xFF475569),
//                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
//                                            lineHeight = 24.sp
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    // Population Card
//
//                    details.population?.let { population ->
//                        item {
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .shadow(4.dp, RoundedCornerShape(16.dp)),
//                                shape = RoundedCornerShape(16.dp),
//                                colors = CardDefaults.cardColors(containerColor = Color.White)
//                            ) {
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(20.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Box(
//                                        modifier = Modifier
//                                            .size(56.dp)
//                                            .clip(CircleShape)
//                                            .background(
//                                                brush = Brush.radialGradient(
//                                                    colors = listOf(
//                                                        Color(0xFF10B981).copy(alpha = 0.2f),
//                                                        Color(0xFF10B981).copy(alpha = 0.1f)
//                                                    )
//                                                )
//                                            ),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Icon(
//                                            Icons.Default.Groups,
//                                            contentDescription = "Population",
//                                            tint = Color(0xFF10B981),
//                                            modifier = Modifier.size(28.dp)
//                                        )
//                                    }
//
//                                    Spacer(modifier = Modifier.width(16.dp))
//
//                                    Column {
//                                        Text(
//                                            text = "Population",
//                                            fontSize = 14.sp,
//                                            color = Color(0xFF64748B),
//                                            fontWeight = FontWeight.Medium
//                                        )
//                                        Text(
//                                            text = population.toString(),
//                                            fontSize = 18.sp,
//                                            fontWeight = FontWeight.SemiBold,
//                                            color = Color(0xFF1E293B)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    // Contact Information Card
//                    item {
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .shadow(4.dp, RoundedCornerShape(16.dp)),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
//                        ) {
//                            Column(
//                                modifier = Modifier.padding(20.dp),
//                                verticalArrangement = Arrangement.spacedBy(12.dp)
//                            ) {
//                                Text(
//                                    text = "Contact Information",
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color(0xFF1E293B),
//                                    modifier = Modifier.padding(bottom = 4.dp)
//                                )
//
//                                // Phone
//                                if(fields?.show_phone == true) {
//
//
//                                    details.phone?.let { phone ->
//                                        val context = LocalContext.current
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .clickable {
//                                                    try {
//                                                        val cleanedNumber =
//                                                            phone.replace(Regex("[\\s-()]"), "")
//                                                        val phoneIntent =
//                                                            Intent(Intent.ACTION_DIAL).apply {
//                                                                data =
//                                                                    Uri.parse("tel:$cleanedNumber")
//                                                            }
//                                                        context.startActivity(phoneIntent)
//                                                    } catch (e: Exception) {
//                                                        println("Error opening phone dialer: ${e.message}")
//                                                    }
//                                                }
//                                        ) {
//                                            Icon(
//                                                Icons.Default.Phone,
//                                                contentDescription = "Phone",
//                                                tint = Color(0xFF10B981),
//                                                modifier = Modifier.size(20.dp)
//                                            )
//                                            Spacer(modifier = Modifier.width(12.dp))
//                                            Column {
//                                                Text(
//                                                    text = "Phone",
//                                                    fontSize = 12.sp,
//                                                    color = Color(0xFF64748B)
//                                                )
//                                                Text(
//                                                    text = phone,
//                                                    fontSize = 16.sp,
//                                                    fontWeight = FontWeight.Medium,
//                                                    color = Color(0xFF10B981)
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                                // Email
//                                if(fields?.show_email == true) {
//
//
//                                    details.email?.let { email ->
//                                        val context = LocalContext.current
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .clickable {
//                                                    try {
//                                                        val emailIntent =
//                                                            Intent(Intent.ACTION_SENDTO).apply {
//                                                                data = Uri.parse("mailto:$email")
//                                                            }
//                                                        context.startActivity(emailIntent)
//                                                    } catch (e: Exception) {
//                                                        println("Error opening email: ${e.message}")
//                                                    }
//                                                }
//                                        ) {
//                                            Icon(
//                                                Icons.Default.Email,
//                                                contentDescription = "Email",
//                                                tint = Color(0xFF10B981),
//                                                modifier = Modifier.size(20.dp)
//                                            )
//                                            Spacer(modifier = Modifier.width(12.dp))
//                                            Column {
//                                                Text(
//                                                    text = "Email",
//                                                    fontSize = 12.sp,
//                                                    color = Color(0xFF64748B)
//                                                )
//                                                Text(
//                                                    text = email,
//                                                    fontSize = 16.sp,
//                                                    fontWeight = FontWeight.Medium,
//                                                    color = Color(0xFF10B981)
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if(fields?.show_tables == true && fields!!.tableData.isNotEmpty()){
//                        item {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .background(Color.White)
//                                    .padding(20.dp)
//                            ) {
//                                SectionHeader(
//                                    icon = Icons.Default.TableChart,
//                                    title = "Tables",
//                                    color = Color(0xFF10B981)
//                                )
//
//                                Spacer(modifier = Modifier.height(12.dp))
//
//                                // All tables go here
//                                fields!!.tableData.forEachIndexed { index, table ->
//                                    SectionHeader(
//                                        icon = null,
//                                        title = table.title,
//                                        color = Color(0xFF10B981)
//                                    )
//
//                                    // Table Container with border
//                                    Surface(
//                                        modifier = Modifier.fillMaxWidth(),
//                                        shape = RoundedCornerShape(8.dp),
//                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
//                                    ) {
//                                        Column {
//                                            // Header Row
//                                            Row(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .background(Color(0xFFF9FAFB))
//                                                    .padding(12.dp)
//                                            ) {
//                                                table.headers.forEach { header ->
//                                                    Text(
//                                                        text = header,
//                                                        modifier = Modifier.weight(1f),
//                                                        style = MaterialTheme.typography.bodyMedium.copy(
//                                                            fontWeight = FontWeight.Bold,
//                                                            color = Color(0xFF374151)
//                                                        )
//                                                    )
//                                                }
//                                            }
//
//                                            Divider(color = Color(0xFFE5E7EB))
//
//                                            // Data Rows
//                                            table.data.forEachIndexed { rowIndex, row ->
//                                                Row(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                        .background(
//                                                            if (rowIndex % 2 == 0) Color.White
//                                                            else Color(0xFFF9FAFB)
//                                                        )
//                                                        .padding(12.dp)
//                                                ) {
//                                                    row.forEach { cell ->
//                                                        Text(
//                                                            text = cell.toString(),
//                                                            modifier = Modifier.weight(1f),
//                                                            style = MaterialTheme.typography.bodySmall.copy(
//                                                                color = Color(0xFF6B7280)
//                                                            )
//                                                        )
//                                                    }
//                                                }
//
//                                                if (rowIndex < table.data.size - 1) {
//                                                    Divider(color = Color(0xFFE5E7EB))
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    // Add spacing between tables (except after the last one)
//                                    if (index < fields!!.tableData.size - 1) {
//                                        Spacer(modifier = Modifier.height(16.dp))
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } ?: run {
//                    item {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(32.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            if (isLoading) {
//                                CircularProgressIndicator(color = Color(0xFF6366F1))
//                            } else {
//                                Text(
//                                    text = "No base details available",
//                                    fontSize = 16.sp,
//                                    color = Color(0xFF64748B)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (showModal && selectedOrganization != null) {
//            OrganizationDetailModal(
//                organization = selectedOrganization!!,
//                onDismiss = {
//                    showModal = false
//                    selectedOrganization = null
//                },
//                onViewDetails = {
//                    showModal = false
//                    showOrganizationDetail = true
//                }
//            )
//        }
//    }
//}

@Composable
fun OrganizationDetailModal(
    organization: Organization,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit
) {
    val animationDuration = 300
    var isVisible by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(animationDuration)
                ) + fadeIn(animationSpec = tween(animationDuration)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(animationDuration)
                ) + fadeOut(animationSpec = tween(animationDuration))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .clickable { },
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Organization Details",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )

                            IconButton(onClick = onDismiss) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF6366F1)
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        organization.type.color.copy(alpha = 0.2f),
                                                        organization.type.color.copy(alpha = 0.1f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!organization.imageUrl.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = organization.imageUrl,
                                                contentDescription = "${organization.name} logo",
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = organization.type.icon,
                                                contentDescription = null,
                                                tint = organization.type.color,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = organization.name,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = organization.type.color.copy(alpha = 0.1f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Text(
                                                text = organization.type.displayName,
                                                fontSize = 14.sp,
                                                color = organization.type.color,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            organization.description?.let { desc ->
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Description",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 14.sp,
                                                color = Color(0xFF64748B),
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }
                            }

                            organization.contact?.let { contact ->
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Phone,
                                                    contentDescription = "Contact",
                                                    tint = Color(0xFF6366F1),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Contact Information",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1E293B)
                                                )
                                            }
                                            val context = LocalContext.current

                                            Text(
                                                text = "DSN: $contact",
                                                fontSize = 16.sp,
                                                color = Color(0xFF6366F1),
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.clickable {
                                                    try{
                                                        val cleanedNumber = contact.replace(Regex("[\\s-()]"), "")
                                                        val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                                                            data = Uri.parse("tel:$cleanedNumber")
                                                        }
                                                        context.startActivity(phoneIntent)
                                                    }catch(e: Exception){
                                                        println("Error opening phone dialer: ${e.message}")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            organization.webUrl?.let { webUrl ->
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Link,
                                                    contentDescription = "Website",
                                                    tint = Color(0xFF10B981),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Website",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1E293B)
                                                )
                                            }

                                            Text(
                                                text = webUrl,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF10B981),
                                                textDecoration = TextDecoration.Underline,
                                                modifier = Modifier.clickable {
                                                    try {
                                                        val url = if (webUrl.startsWith("http://") ||
                                                            webUrl.startsWith("https://")) {
                                                            webUrl
                                                        } else {
                                                            "https://$webUrl"
                                                        }
                                                        uriHandler.openUri(url)
                                                    } catch (e: Exception) {
                                                        println("Error opening URL: ${e.message}")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = "Info",
                                                tint = Color(0xFF8B5CF6),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Organization ID",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                        Text(
                                            text = organization.id,
                                            fontSize = 14.sp,
                                            color = Color(0xFF64748B),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = onViewDetails,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = organization.type.color
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = "View Details",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("View Details")
                                    }

                                    OutlinedButton(
                                        onClick = { },
                                        modifier = Modifier.weight(1f),
                                        border = BorderStroke(1.dp, organization.type.color),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = organization.type.color,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Share", color = organization.type.color)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Add the new OrganizationDetailScreen composable here
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationDetailScreen(
    organization: Organization,
    onBackPressed: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    val links = organization.links ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Sticky Top Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
            color = Color.White,
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
                        tint = Color(0xFF6366F1)
                    )
                }

                Text(
                    text = organization.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /* Add share functionality */ }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF6366F1)
                    )
                }
            }
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Organization Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            organization.primaryColor?.let {
                                Color(android.graphics.Color.parseColor(it))
                            } ?: Color.White
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        organization.type.color.copy(alpha = 0.2f),
                                        organization.type.color.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!organization.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = organization.imageUrl,
                                contentDescription = "${organization.name} logo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = organization.type.icon,
                                contentDescription = null,
                                tint = organization.type.color,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = organization.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = organization.textColor?.let {
                            Color(it.toColorInt())
                        } ?: Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Building ${organization.building_number}",
                        fontSize = 14.sp,
                        color = (organization.textColor?.let {
                            Color(it.toColorInt())
                        } ?: Color(0xFF1E293B)).copy(alpha = 0.7f)
                    )

                    Text(
                        text = organization.address ?: "N/A",
                        fontSize = 14.sp,
                        color = (organization.textColor?.let {
                            Color(it.toColorInt())
                        } ?: Color(0xFF1E293B)).copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Description Section
            organization.description?.let { desc ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        SectionHeader(
                            icon = Icons.Default.Description,
                            title = "About",
                            color = Color(0xFF6366F1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = desc,
                            fontSize = 15.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 22.sp
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            // Contact Information
            organization.contact?.let { contact ->
                item {
                    val context = LocalContext.current

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(20.dp)
                    ) {

                        ContactItem(
                            icon = Icons.Default.Phone,
                            label = "DSN",
                            value = contact,
                            onClick = {
                                try {
                                    val cleanedNumber = contact.replace(Regex("[\\s-()]"), "")
                                    val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = "tel:$cleanedNumber".toUri()
                                    }
                                    context.startActivity(phoneIntent)
                                } catch (e: Exception) {
                                    println("Error opening phone dialer: ${e.message}")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ContactItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = organization.email,
                            onClick = {
                                try {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:${organization.email}".toUri()
                                    }
                                    context.startActivity(emailIntent)
                                } catch (e: Exception) {
                                    println("Error opening email: ${e.message}")
                                }
                            }
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            // Website
            organization.webUrl?.let { webUrl ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        SectionHeader(
                            icon = Icons.Default.Language,
                            title = "Website",
                            color = Color(0xFF10B981)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LinkItem(
                            label = null,
                            url = webUrl,
                            onClick = {
                                try {
                                    val url = if (webUrl.startsWith("http://") ||
                                        webUrl.startsWith("https://")
                                    ) {
                                        webUrl
                                    } else {
                                        "https://$webUrl"
                                    }
                                    uriHandler.openUri(url)
                                } catch (e: Exception) {
                                    println("Error opening URL: ${e.message}")
                                }
                            }
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            // Additional Links
                if (links.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(20.dp)
                        ) {
                            SectionHeader(
                                icon = Icons.Default.Link,
                                title = "Links",
                                color = Color(0xFF10B981)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            links.forEachIndexed { index, link ->
                                LinkItem(
                                    label = link.label,
                                    url = link.link,
                                    showDivider = index < links.size - 1,
                                    onClick = {
                                        try {
                                            val url = if (link.link.startsWith("http://") ||
                                                link.link.startsWith("https://")
                                            ) {
                                                link.link
                                            } else {
                                                "https://${link.link}"
                                            }
                                            uriHandler.openUri(url)
                                        } catch (e: Exception) {
                                            println("Error opening URL: ${e.message}")
                                        }
                                    }
                                )
                            }
                        }
                    }

            }
            if (organization.useTable && organization.tableData.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        SectionHeader(
                            icon = Icons.Default.TableChart,
                            title = "Tables",
                            color = Color(0xFF10B981)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // All tables go here
                        organization.tableData.forEachIndexed { index, table ->
                            SectionHeader(
                                icon = null,
                                title = table.title,
                                color = Color(0xFF10B981)
                            )

                            // Table Container with border
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                            ) {
                                Column {
                                    // Header Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF9FAFB))
                                            .padding(12.dp)
                                    ) {
                                        table.headers.forEach { header ->
                                            Text(
                                                text = header,
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF374151)
                                                )
                                            )
                                        }
                                    }

                                    Divider(color = Color(0xFFE5E7EB))

                                    // Data Rows
                                    table.data.forEachIndexed { rowIndex, row ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (rowIndex % 2 == 0) Color.White
                                                    else Color(0xFFF9FAFB)
                                                )
                                                .padding(12.dp)
                                        ) {
                                            row.forEach { cell ->
                                                Text(
                                                    text = cell.toString(),
                                                    modifier = Modifier.weight(1f),
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF6B7280)
                                                    )
                                                )
                                            }
                                        }

                                        if (rowIndex < table.data.size - 1) {
                                            Divider(color = Color(0xFFE5E7EB))
                                        }
                                    }
                                }
                            }

                            // Add spacing between tables (except after the last one)
                            if (index < organization.tableData.size - 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }


        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector?,
    title: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        icon?.let{
            Icon(
                it,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF6366F1),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LinkItem(
    label: String?,
    url: String,
    showDivider: Boolean = false,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if(label != null){
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = url,
                    fontSize = 13.sp,
                    color = Color(0xFF10B981),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                Icons.Default.OpenInNew,
                contentDescription = "Open",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(18.dp)
            )
        }

        if (showDivider) {
            Divider(
                modifier = Modifier.padding(start = 12.dp),
                color = Color(0xFFF1F5F9)
            )
        }
    }
}

// Keep the existing OrganizationCard and Preview composables...

@Composable
fun OrganizationCard(
    organization: Organization,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .border(
                width = 1.dp,
                color = organization.secondaryColor?.let {
                    Color(android.graphics.Color.parseColor(it))
                } ?: organization.type.color,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = organization.primaryColor?.let {
                        Color(android.graphics.Color.parseColor(it))
                    } ?: Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        )
        {
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
                                    organization.type.color.copy(alpha = 0.2f),
                                    organization.type.color.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!organization.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = organization.imageUrl,
                            contentDescription = "${organization.name} logo",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = organization.type.icon,
                            contentDescription = null,
                            tint = organization.type.color,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = organization.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(android.graphics.Color.parseColor(organization.textColor ?: "#000000")),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = organization.type.color.copy(alpha = 0.1f),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = organization.type.displayName,
                            fontSize = 12.sp,
                            color = organization.type.color,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp)
                        )
                    }

                    organization.description?.let { desc ->
                        Text(
                            text = desc,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    organization.contact?.let { contact ->
                        Text(
                            text = "DSN: $contact",
                            fontSize = 12.sp,
                            color = Color(0xFF6366F1),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseDetailScreenPreview() {
    val sampleBase = Base(
        id = "1",
        name = "Joint Base Andrews",
        city = "Andrews",
        state = "MD",
        active = true
    )

    MaterialTheme {
        BaseDetailScreen(
            base = sampleBase
        )
    }
}