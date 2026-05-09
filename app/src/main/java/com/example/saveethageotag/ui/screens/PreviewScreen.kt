package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saveethageotag.ui.theme.DarkBackground
import com.example.saveethageotag.ui.theme.PrimaryGreen
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.theme.TextSecondary

@Composable
fun PreviewScreen(onConfirm: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
            }
            Text(
                "Preview & Confirm", 
                color = Color.Black, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp)) // To balance the back button
        }

        // Image Preview with Overlays
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            // Mock Building Image (Placeholder)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(100.dp))
            }

            // GPS Map Camera Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("GPS Map Camera", color = Color.White, fontSize = 10.sp)
            }

            // Bottom Location Overlay on Image
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    // Small Map Placeholder
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Chennai, Tamil Nadu, India", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            // Small flag placeholder
                            Box(modifier = Modifier.size(12.dp, 8.dp).background(Color.White))
                        }
                        Text("Saveetha Nagar, Thandalam,", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text("Chennai, Tamil Nadu 602105, India", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text("Lat 13.047843, Long 80.052082", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text("Saturday, 09/05/2026 10:39 AM GMT+05:30", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                    }
                }
            }
        }

        // Details Panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DetailRow(Icons.Default.Schedule, "Date & Time", "09 May 2026, 10:39 AM")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailRow(Icons.Default.GpsFixed, "Accuracy", "6.5 m")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailRow(Icons.Default.Smartphone, "Device", "Samsung Galaxy S23 Ultra")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm & Generate QR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreenPreview() {
    SaveethaGeotagTheme {
        PreviewScreen(onConfirm = {})
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
