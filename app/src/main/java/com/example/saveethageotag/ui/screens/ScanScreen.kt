package com.example.saveethageotag.ui.screens

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.PhotoLibrary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(onScanSuccess: (String) -> Unit) {
    val isPreview = LocalInspectionMode.current
    
    if (isPreview) {
        ScanContent(onScanSuccess)
    } else {
        ScanScreenWithPermission(onScanSuccess)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ScanScreenWithPermission(onScanSuccess: (String) -> Unit) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        ScanContent(onScanSuccess)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required to scan QR codes", modifier = Modifier.padding(16.dp))
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun ScanContent(onScanSuccess: (String) -> Unit) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraProviderFuture = remember(isPreview) { 
        if (isPreview) null else ProcessCameraProvider.getInstance(context) 
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Camera Viewfinder
        if (isPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text("QR Scanner Preview Placeholder", color = Color.White)
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val mainExecutor = ContextCompat.getMainExecutor(ctx)
                    imageAnalysis.setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        QRAnalyzer { result ->
                            mainExecutor.execute {
                                Log.d("GeoProof_Scanner", "scanner result: $result")
                                onScanSuccess(result)
                            }
                        }
                    )

                    cameraProviderFuture?.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                        } catch (e: Exception) {
                            Log.e("GeoProof_Scanner", "Use case binding failed", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Frame Overlay
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp, 280.dp)
                    .align(Alignment.Center)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                // Corner Accents
                val cornerSize = 24.dp
                val strokeWidth = 4.dp
                
                Box(modifier = Modifier.size(cornerSize).align(Alignment.TopStart).border(strokeWidth, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(topStart = 12.dp)))
                Box(modifier = Modifier.size(cornerSize).align(Alignment.TopEnd).border(strokeWidth, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(topEnd = 12.dp)))
                Box(modifier = Modifier.size(cornerSize).align(Alignment.BottomStart).border(strokeWidth, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(bottomStart = 12.dp)))
                Box(modifier = Modifier.size(cornerSize).align(Alignment.BottomEnd).border(strokeWidth, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(bottomEnd = 12.dp)))
            }
        }

        // Floating Back Button
        IconButton(
            onClick = { /* Back handled by system or navigation */ },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 320.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Align the QR CODE within the frame", color = Color.White, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    try {
                        val image = InputImage.fromFilePath(context, it)
                        val scanner = BarcodeScanning.getClient()
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty()) {
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { content ->
                                            onScanSuccess(content)
                                        }
                                    }
                                } else {
                                    // Image recognized but no QR found
                                    android.widget.Toast.makeText(context, "No QR Code recognized in this image", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                android.widget.Toast.makeText(context, "Image recognition failed", android.widget.Toast.LENGTH_SHORT).show()
                            }
                    } catch (e: Exception) {
                        Log.e("ScanScreen", "Gallery scan failed", e)
                        android.widget.Toast.makeText(context, "Error opening image", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select from Gallery")
            }
        }
    }
}

class QRAnalyzer(private val onScanSuccess: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val barcodeScanner = BarcodeScanning.getClient()
    private var isScanning = true

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScanning) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (isScanning && barcodes.isNotEmpty()) {
                        for (barcode in barcodes) {
                            val rawValue = barcode.rawValue ?: continue
                            isScanning = false
                            onScanSuccess(rawValue)
                            break
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
