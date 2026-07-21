package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme

import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage

import com.example.saveethageotag.ui.viewmodels.DetailsViewModel
import com.example.saveethageotag.ui.viewmodels.DetailsState
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailsScreen(
    verificationId: String = "GP25A8X7K9L2",
    viewModel: DetailsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onAnalysisClick: () -> Unit = {}
) {
    val state = viewModel.uiState.value
    val context = androidx.compose.ui.platform.LocalContext.current
    var showFullscreenImage by remember { mutableStateOf(false) }
    
    LaunchedEffect(verificationId) {
        viewModel.fetchDetails(verificationId)
    }

    val dateString = if (state.timestamp > 0) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(state.timestamp))
    } else if (state.isLoading) {
        "Fetching..."
    } else {
        "N/A"
    }

    if (showFullscreenImage) {
        FullscreenImageDialog(state) { showFullscreenImage = false }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.fetchDetails(verificationId) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Retry Verification Search")
                    }
                }
            } else {
                // Location Info Card (Matching image overlay style)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        // Actual Image Preview - CLICKABLE
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .clickable { showFullscreenImage = true },
                            contentAlignment = Alignment.Center
                        ) {
                            val imageSource: Any? = remember(state.localImagePath, state.imageUrl, state.imageBitmap) {
                                when {
                                    state.imageBitmap != null -> state.imageBitmap
                                    !state.imageUrl.isNullOrEmpty() -> state.imageUrl
                                    !state.localImagePath.isNullOrEmpty() && java.io.File(state.localImagePath).exists() -> java.io.File(state.localImagePath)
                                    else -> null
                                }
                            }
                            
                            if (imageSource != null) {
                                AsyncImage(
                                    model = imageSource,
                                    contentDescription = "Captured Photo",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    onError = {
                                        Log.e("DetailsScreen", "Failed to load image: ${it.result.throwable.message}")
                                    }
                                )
                            } else {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.saveethageotag.R.drawable.saveetha_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp).padding(4.dp)
                                )
                            }
                            
                            // Add magnifying glass icon
                            Icon(
                                Icons.Default.ZoomIn, 
                                contentDescription = null, 
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(state.address.split(",").firstOrNull() ?: "Location", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(state.address.ifEmpty { "Loading address..." }, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, maxLines = 3)
                            Text("Lat ${state.latitude}, Long ${state.longitude}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(dateString, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                }

                // Details List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DetailsDetailItemRow("Identification Code", state.verificationCode ?: verificationId, isGreen = true)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    DetailsDetailItemRow("Captured on", dateString)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    DetailsDetailItemRow("Captured by", state.userName ?: "Anonymous User")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    DetailsDetailItemRow("Device", state.deviceModel ?: android.os.Build.MODEL)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    DetailsDetailItemRow("Accuracy", state.accuracy)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    
                    val statusText = if (state.isSynced) "Authentic (Cloud Verified)" else "Authentic (Local Data)"
                    val statusColor = if (state.isSynced) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Security Status", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.isSynced) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                statusText, 
                                color = statusColor,
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // QR Code Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Verification QR Code", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val qrBitmap = remember(state.verificationCode) {
                            Log.d("DetailsScreen", "Generating QR for: ${state.verificationCode}")
                            state.verificationCode?.let { com.example.saveethageotag.utils.QRGenerator.generateQRCode(it, 400) }
                        }
                        
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier.size(150.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAnalysisClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View AI Analysis Report", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { generatePdfReport(context, state, dateString) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download PDF Report", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Floating Header Overlay (Transparent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            IconButton(
                onClick = { shareReport(context, state, dateString) },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
        }
    }
}

@Composable
fun FullscreenImageDialog(state: DetailsState, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            val imageSource = remember(state.localImagePath, state.imageUrl, state.imageBitmap) {
                when {
                    state.imageBitmap != null -> state.imageBitmap
                    !state.imageUrl.isNullOrEmpty() -> state.imageUrl
                    !state.localImagePath.isNullOrEmpty() && java.io.File(state.localImagePath).exists() -> java.io.File(state.localImagePath)
                    else -> null
                }
            }

            AsyncImage(
                model = imageSource,
                contentDescription = "Full Screen View",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

fun shareReport(context: Context, state: DetailsState, dateString: String) {
    val shareLink = "https://saveetha-geotag.com/verify/${state.verificationCode}"
    val shareIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_SUBJECT, "Security Verification: ${state.verificationCode}")
        putExtra(android.content.Intent.EXTRA_TEXT, """
            📸 SECURITY VERIFICATION REPORT
            -------------------------------
            Status: ✅ AUTHENTIC
            Code: ${state.verificationCode}
            Captured on: $dateString
            
            Verify directly in app: $shareLink
            
            LOCATION DETAILS
            Address: ${state.address}
            Coordinates: ${state.latitude}, ${state.longitude}
            
            Verified by Saveetha Geotag Security
            -------------------------------
        """.trimIndent())
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Report"))
}

fun generatePdfReport(context: Context, state: DetailsState, dateString: String) {
    try {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
            isAntiAlias = true
        }

        // Title
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("SECURITY VERIFICATION REPORT", 50f, 60f, paint)

        // Metadata Header
        paint.textSize = 16f
        paint.color = android.graphics.Color.DKGRAY
        canvas.drawText("Verification Details", 50f, 100f, paint)

        // Content
        var y = 140f
        val lineGap = 30f
        
        fun drawRow(label: String, value: String) {
            textPaint.isFakeBoldText = true
            canvas.drawText("$label:", 50f, y, textPaint)
            textPaint.isFakeBoldText = false
            canvas.drawText(value, 200f, y, textPaint)
            y += lineGap
        }

        drawRow("Status", "✅ AUTHENTIC")
        drawRow("Code", state.verificationCode ?: "N/A")
        drawRow("Date", dateString)
        drawRow("Coordinates", "${state.latitude}, ${state.longitude}")
        drawRow("Accuracy", state.accuracy)
        drawRow("Device", android.os.Build.MODEL)
        
        y += 20f
        textPaint.isFakeBoldText = true
        canvas.drawText("Full Address:", 50f, y, textPaint)
        textPaint.isFakeBoldText = false
        y += 20f
        
        // Multi-line address wrapping
        val words = state.address.split(" ")
        var line = ""
        for (word in words) {
            if (textPaint.measureText("$line $word") < 450f) {
                line = if (line.isEmpty()) word else "$line $word"
            } else {
                canvas.drawText(line, 50f, y, textPaint)
                y += 20f
                line = word
            }
        }
        canvas.drawText(line, 50f, y, textPaint)

        y += 50f
        paint.textSize = 12f
        paint.color = android.graphics.Color.GRAY
        canvas.drawText("This report serves as a digital proof of location and time authenticity.", 50f, y, paint)
        y += 20f
        canvas.drawText("Generated by Saveetha Geotag App.", 50f, y, paint)

        pdfDocument.finishPage(page)

        // Save to Downloads using MediaStore for modern Android compatibility
        val fileName = "GeoProof_${state.verificationCode?.take(8)}.pdf"
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val resolver = context.contentResolver
        val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            // Fallback for older versions would go here
            resolver.insert(android.provider.MediaStore.Files.getContentUri("external"), contentValues)
        }

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            android.widget.Toast.makeText(context, "Report saved to Downloads Folder", android.widget.Toast.LENGTH_LONG).show()
        }
        
        pdfDocument.close()
    } catch (e: Exception) {
        Log.e("DetailsScreen", "Failed to generate PDF", e)
        android.widget.Toast.makeText(context, "Failed to download PDF: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
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
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(
            value, 
            color = if (isGreen) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
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
