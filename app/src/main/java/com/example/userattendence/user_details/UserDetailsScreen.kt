package com.example.userattendence.user_details

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel            // ✅ Fix for viewModel()
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage        // ✅ Glide instead of Coil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.userattendence.attendance.AttendanceRecord
import com.example.userattendence.utils.getCurrentLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun UserDetails(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: UserDetailsViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var captureState by remember { mutableStateOf(CaptureState()) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    var tempFilePath by remember { mutableStateOf("") }    // ✅ Add this


    val records by viewModel.records.collectAsState()

    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (!permissions.allPermissionsGranted) {
            permissions.launchMultiplePermissionRequest()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // ✅ One single state update — no shaking
            captureState = CaptureState(
                imageUri = tempUri,
                isLoading = true,
                filePath = tempFilePath,
            )
            scope.launch {
                val locationData = getCurrentLocation(context)
                // ✅ One final state update when location is ready
                captureState = CaptureState(
                    imageUri = tempUri,
                    address = locationData?.address ?: "Location unavailable",
                    isLoading = false,
                    filePath = tempFilePath,
                )
                locationData?.let {
                    viewModel.saveAttendance(
                        context = context,
                        locationData = it,
                        imageUri = tempFilePath,
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User Attendance",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (permissions.allPermissionsGranted) {
                    val (uri, path) = createImageFile(context)
                    tempUri = uri
                    tempFilePath = path
                    cameraLauncher.launch(uri)
                } else {
                    permissions.launchMultiplePermissionRequest()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                if (permissions.allPermissionsGranted)
                    "📷 Capture Attendance"
                else
                    "Grant Permissions"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Card only recomposes when captureState changes as a whole
        if (captureState.imageUri != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Latest Capture",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ Glide with explicit bitmap transformation for dark image fix
                    // ✅ GlideImage uses file path — no stream rewind issue
                    GlideImage(
                        model = captureState.filePath,    // ✅ File path, not content:// URI
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    ) {
                        it.diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ Single condition check — no flickering between states
                    when {
                        captureState.isLoading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Fetching location...", fontSize = 13.sp, color = Color.Gray)
                            }
                        }

                        captureState.address.isNotEmpty() -> {
                            Text(
                                "📍 ${captureState.address}",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (records.isNotEmpty()) {
            Text(
                "Attendance History",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(records) { record ->
                    AttendanceRecordCard(record)
                }
            }
        }
    }
}

// ── Single history card ──────────────────────────────────────────────
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AttendanceRecordCard(record: AttendanceRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {

            GlideImage(
                model = record.imageUri,    // ✅ already file path now
                contentDescription = "Attendance Photo",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            ) {
                it.diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(record.capturedAt, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("📍 ${record.address}", fontSize = 12.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Lat: ${"%.4f".format(record.latitude)}, Lng: ${"%.4f".format(record.longitude)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ── Helper to create image file URI ─────────────────────────────────
// ✅ Returns both URI (for camera) and path (for Glide)
fun createImageFile(context: Context): Pair<Uri, String> {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "IMG_$timeStamp.jpg"
    )
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
    return Pair(uri, imageFile.absolutePath)   // ✅ return file path too
}