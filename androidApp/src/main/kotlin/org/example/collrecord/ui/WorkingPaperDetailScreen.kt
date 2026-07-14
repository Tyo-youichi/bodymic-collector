package org.example.collrecord.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.collrecord.model.WorkingPaper
import org.example.collrecord.ui.theme.CollectorBlue
import org.example.collrecord.ui.theme.accentColorFor

@Composable
fun WorkingPaperDetailScreen(
    task: WorkingPaper,
    onBack: () -> Unit,
    onStartVisit: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val accent = accentColorFor(task.businessUnit)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { TextButton(onClick = onBack) { Text("Tutup") } },
                actions = { TextButton(onClick = onHistoryClick) { Text("Riwayat") } }
            )
        },
        bottomBar = {
            Button(
                onClick = onStartVisit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CollectorBlue)
            ) {
                Text("Mulai Kunjungan & Rekam")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .width(4.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accent.solid)
                ) {}
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    task.debiturName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(accent.solid)
                ) {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoRow(
                icon = "🕒",
                primary = "Jatuh Tempo: ${task.dueDate}",
                secondary = "Overdue ${task.overdue} hari • Tenor ${task.tenor} bulan"
            )
            HorizontalDivider()
            InfoRow(
                icon = "📍",
                primary = task.unit,
                secondary = task.businessUnit
            )
            HorizontalDivider()
            InfoRow(
                icon = "📄",
                primary = "No. Kontrak: ${task.noContract}",
                secondary = "Customer ID: ${task.customerId}"
            )
            HorizontalDivider()
            InfoRow(
                icon = "💰",
                primary = "Total Tagihan: ${formatRupiah(task.total)}",
                secondary = "Billing ${formatRupiah(task.billing)} • Denda ${formatRupiah(task.denda)}"
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun InfoRow(icon: String, primary: String, secondary: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, modifier = Modifier.width(32.dp))
        Column {
            Text(primary, fontWeight = FontWeight.Medium)
            if (secondary != null) {
                Text(secondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatRupiah(amount: Long): String =
    "Rp" + amount.toString().reversed().chunked(3).joinToString(".").reversed()
