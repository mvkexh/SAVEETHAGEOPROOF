package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
fun VerifiedScreen(onViewDetails: () -> Unit) {
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
                "QR Generated", 
                color = Color.Black, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Success Header
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("QR Code Generated", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Successfully", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // Image Preview with QR
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            // Mock Image Overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(100.dp))
            }

            // Location/QR Overlay (Matching image)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Small Map
                    Box(modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chennai, Tamil Nadu, India", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Saveetha Nagar, Thandalam...", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                    }
                    // QR Code
                    Box(
                        modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.size(35.dp))
                    }
                }
            }
        }

        // Details List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            VerifiedDetailItemRow("Verification ID", "GP25A8X7K9L2", isHighlighted = true)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
            VerifiedDetailItemRow("Captured on", "09 May 2026, 10:39 AM")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("View Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun VerifiedDetailItemRow(label: String, value: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        if (isHighlighted) {
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    value, 
                    color = PrimaryGreen, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        } else {
            Text(value, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifiedScreenPreview() {
    SaveethaGeotagTheme {
        VerifiedScreen(onViewDetails = {})
    }
}
