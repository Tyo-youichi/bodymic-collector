package org.example.collrecord.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.viewmodel.RecordingUiState
import org.example.collrecord.viewmodel.RecordingViewModel

@Composable
fun RecordingScreen(
    task: WorkingPaper,
    instanceKey: String,
    onFinished: () -> Unit,
    onBack: () -> Unit,
    viewModel: RecordingViewModel = viewModel(key = instanceKey)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermissions = result.values.all { it }
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is RecordingUiState.Uploaded) {
            onFinished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kunjungan: ${task.debiturName}") },
                navigationIcon = {
                    TextButton(onClick = {
                        viewModel.cancelRecording()
                        onBack()
                    }) { Text("Kembali") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No. Kontrak: ${task.noContract}")
            Text("Unit: ${task.unit}")
            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is RecordingUiState.Idle -> {
                    Button(
                        onClick = { viewModel.startRecording(task.taskId) },
                        enabled = hasPermissions
                    ) { Text("Mulai Rekam") }
                    if (!hasPermissions) {
                        Text("Menunggu izin mic & lokasi...")
                    }
                }
                is RecordingUiState.Recording -> {
                    Text("Sedang merekam...")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.finishVisit(task.taskId) }) {
                        Text("Selesai & Kirim")
                    }
                }
                is RecordingUiState.Finishing -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menyimpan lokasi & menjadwalkan upload...")
                }
                is RecordingUiState.Uploaded -> {
                    Text("Selesai, upload dijadwalkan di background.")
                }
                is RecordingUiState.Error -> {
                    Text("Error: ${state.message}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.startRecording(task.taskId) }) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
    }
}
