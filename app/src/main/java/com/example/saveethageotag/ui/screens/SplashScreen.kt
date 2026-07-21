package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saveethageotag.R
import com.example.saveethageotag.ui.theme.LogoBlue
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main Logo and Branding (Centered)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.saveetha_logo),
                contentDescription = "Saveetha Geotag Logo",
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tagline with color accents
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.width(24.dp).height(2.dp).background(Color(0xFF8BC34A)))
                Text(
                    "  CAPTURE. VERIFY. TRUST.  ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64),
                    letterSpacing = 2.sp
                )
                Box(modifier = Modifier.width(24.dp).height(2.dp).background(Color(0xFF8BC34A)))
            }
        }

        // Bottom Decorative Waves and Icon Info (Mimicking the image)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .align(Alignment.BottomCenter)
        ) {
            // Background Wave Layer 1 (Green accent)
            Surface(
                modifier = Modifier.fillMaxSize().padding(top = 20.dp),
                color = Color(0xFF689F38), // Green from the image
                shape = RoundedCornerShape(topStart = 80.dp)
            ) {}
            // Background Wave Layer 2 (Primary Blue)
            Surface(
                modifier = Modifier.fillMaxSize().padding(top = 45.dp),
                color = LogoBlue,
                shape = RoundedCornerShape(topStart = 100.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SplashIconItem(icon = Icons.Outlined.CameraAlt, label = "CAPTURE")
                        
                        // Vertical divider
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.3f)))
                        
                        SplashIconItem(icon = Icons.Outlined.Shield, label = "VERIFY")
                        
                        // Vertical divider
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.3f)))
                        
                        SplashIconItem(icon = Icons.Outlined.Handshake, label = "TRUST")
                    }
                }
            }
        }
    }
}

@Composable
fun SplashIconItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SaveethaGeotagTheme {
        SplashScreen()
    }
}
