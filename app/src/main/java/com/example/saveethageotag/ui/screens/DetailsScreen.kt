package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun DetailsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
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
                "Verification Details", 
                color = Color.Black, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            IconButton(onClick = {}) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
            }
        }

        // Location Info Card (Matching image overlay style)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                // Mock Map Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Chennai, Tamil Nadu, India", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(16.dp, 10.dp).background(Color.White))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Saveetha Nagar, Thandalam,", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text("Chennai, Tamil Nadu 602105, India", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text("Lat 13.047843, Long 80.052082", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text("Saturday, 09/05/2026 10:39 AM GMT+05:30", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text("Note : Captured by GPS Map Camera", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                }
            }
        }

        // Details List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            DetailsDetailItemRow("Verification ID", "GP25A8X7K9L2", isGreen = true)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("Captured on", "09 May 2026, 10:39 AM")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("Timezone", "IST (UTC +05:30)")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("Accuracy", "6.5 m")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("Device", "Samsung Galaxy S23 Ultra")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("Captured by", "GPS Map Camera")
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("AI Analysis", "No tampering detected", isGreen = true)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            DetailsDetailItemRow("Status", "Authentic", isGreen = true)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Share / Download Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun DetailsDetailItemRow(label: String, value: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(
            value, 
            color = if (isGreen) PrimaryGreen else Color.Black, 
            fontSize = 14.sp, 
            fontWeight = if (isGreen) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    SaveethaGeotagTheme {
        DetailsScreen()
    }
}
