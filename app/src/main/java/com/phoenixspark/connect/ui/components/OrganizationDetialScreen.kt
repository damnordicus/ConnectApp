package com.phoenixspark.connect.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phoenixspark.connect.data.Link
import com.phoenixspark.connect.data.Organization
import com.phoenixspark.connect.ui.components.*
import kotlin.collections.emptyList

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


            // Tables
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

                        organization.tableData.forEachIndexed { index, table ->
                            TableCard(table = table)

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