package com.example.saveethageotag.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.utils.QRGenerator
import com.example.saveethageotag.ui.viewmodels.DetailsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedScreen(
    verificationId: String,
    viewModel: DetailsViewModel = viewModel(),
    onBack: () -> Unit,
    onViewDetails: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState by viewModel.uiState
    
    LaunchedEffect(verificationId) {
        viewModel.fetchDetails(verificationId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulse animation for the checkmark
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(80.dp).scale(scale),
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9) // Light Green
                    ) {}
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50), // Green
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Text(
                "Authenticity Confirmed",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                "Digital Secure Identification Generated",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Verification Card
            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(24.dp)).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "IDENTIFICATION CODE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val qrBitmap = remember(uiState.verificationCode) {
                            uiState.verificationCode?.let { QRGenerator.generateQRCode(it, 512) }
                        }
                        qrBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: CircularProgressIndicator()
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // UUID Box (Image 4 style)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                uiState.verificationCode?.let {
                                    clipboardManager.setText(AnnotatedString(it))
                                    Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                }
                            },
                        color = Color(0xFFF5F6FF), // Light Blue tint
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E3FF))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = uiState.verificationCode ?: "Generating...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Bottom Buttons
            Button(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Visibility, null)
                Spacer(Modifier.width(8.dp))
                Text("View Full Security Report", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Return to Camera", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

// Extension to use .scale() modifier easily
@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))

@Preview(showBackground = true)
@Composable
fun VerifiedScreenPreview() {
    SaveethaGeotagTheme {
        VerifiedScreen(verificationId = "8cfed66d-e80b-4ffc-b1bf-7f0c5fb5d044", onBack = {}, onViewDetails = {})
    }
}
