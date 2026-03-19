package com.sznurczak.jf_inventur.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sznurczak.jf_inventur.R
import com.sznurczak.jf_inventur.data.tag.ScanResult
import com.sznurczak.jf_inventur.ui.viewmodels.SettingsViewModel
import com.sznurczak.jf_inventur.data.tag.TagData


@Composable
fun MainScreen(modifier: Modifier = Modifier,viewModel: SettingsViewModel,scannedData: ScanResult?)
{


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "JF Inventur System",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            ScanResultCard(scannedData, onClear = {})


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    when (scannedData) {
                        is ScanResult.KeyValueData -> {
                            scannedData.data.forEach { (key, value) ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "$key: ", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = value, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        is ScanResult.SimpleId -> {
                            Text(text = "ID: ${scannedData.id}")
                        }
                        is ScanResult.Error -> {
                            Text(text = "Error: ${scannedData.message}")
                        }
                        null -> {
                            Text(text = "Scanned Tag ID: None")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {}
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Small Top App Bar") }
            )
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}


@Composable
fun ScanCard(
    scannedData: TagData?,
    modifier: Modifier = Modifier
)
{
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    )
    {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Name", style = MaterialTheme.typography.bodySmall)
                    Text("Größe", style = MaterialTheme.typography.bodySmall)
                    Text("", style = MaterialTheme.typography.bodySmall)

                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ///Var placeholder space
                }
            }
        }


    }



}


@Composable
fun ScanResultCard(
    scannedData: ScanResult?,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Icon and Type
            Icon(
                imageVector = Icons.Default.Info, // Replace with boot/jacket icon
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Gefundenes Ausrüstungsteil",
                style = MaterialTheme.typography.headlineSmall
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))

            // Variable Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ResultRow(label = "ID", value = when(scannedData) {
                    is ScanResult.SimpleId -> scannedData.id
                    is ScanResult.KeyValueData -> scannedData.data["id"] ?: "Unbekannt"
                    else -> "N/A"
                })

                // These would eventually come from DB
                ResultRow(label = "Besitzer", value = "Max Mustermann")
                ResultRow(label = "Größe", value = "42")
                ResultRow(label = "Seite", value = "Links")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button to reset state
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nächster Scan")
            }

        }
    }
}



@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

