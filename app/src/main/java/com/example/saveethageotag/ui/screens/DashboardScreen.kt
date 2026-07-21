package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saveethageotag.ui.viewmodels.CapturesViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: CapturesViewModel = viewModel()) {
    val captures by viewModel.captures
    val isLoading by viewModel.isLoading

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.fetchCaptures()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "GEO-PROOF ANALYTICS",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Verified Total",
                        value = captures.size.toString(),
                        icon = Icons.Default.Verified,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    
                    val syncCount = captures.count { it.imageUrl.isNotEmpty() }
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Cloud Synced",
                        value = "$syncCount/${captures.size}",
                        icon = Icons.Default.CloudDone,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "SECURITY STATUS",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SecurityItem(Icons.Default.Shield, "Anti-Tamper Protection", if (captures.isNotEmpty()) "Active" else "Idle")
                        SecurityItem(Icons.Default.LocationOn, "GPS Encryption", if (captures.isNotEmpty()) "Active" else "Idle")
                        SecurityItem(Icons.Default.QrCode, "QR Digital Signature", if (captures.isNotEmpty()) "Secure" else "Idle")
                    }
                }
            }

            item {
                Text(
                    "RECENT VERIFICATIONS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (isLoading && captures.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (captures.isEmpty()) {
                item {
                    Text("No activity recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
            } else {
                items(captures.take(5).size) { index ->
                    val capture = captures[index]
                    val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(capture.timestamp))
                    
                    ListItem(
                        headlineContent = { Text(capture.address.split(",").firstOrNull() ?: "Unknown", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(date) },
                        leadingContent = {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        trailingContent = {
                            Text(
                                "#${capture.verificationCode.takeLast(4).uppercase()}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SecurityItem(icon: ImageVector, label: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp)
        Text(status, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
