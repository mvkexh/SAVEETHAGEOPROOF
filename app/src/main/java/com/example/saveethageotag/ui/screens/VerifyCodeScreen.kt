package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun VerifyCodeScreen(onVerify: () -> Unit) {
    var code by remember { mutableStateOf("") }

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
                "Verify Image", 
                color = Color.Black, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shield Icon
            Icon(
                Icons.Default.VerifiedUser, 
                contentDescription = null, 
                tint = PrimaryGreen, 
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Enter Verification ID to", 
                color = Color.Black, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )
            Text(
                "check authenticity", 
                color = Color.Black, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                placeholder = { Text("Enter Verification ID") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.2f),
                    cursorColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Verify Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(48.dp))

            // How it works
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("How it works?", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                WorkStep("1", "Enter Verification ID")
                WorkStep("2", "We'll check the authenticity")
                WorkStep("3", "View verification results")
            }
        }
    }
}

@Composable
fun WorkStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = PrimaryGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {
    SaveethaGeotagTheme {
        VerifyCodeScreen(onVerify = {})
    }
}
