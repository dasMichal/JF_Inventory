package com.sznurczak.jf_inventur.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sznurczak.jf_inventur.data.SettingsViewModel
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import kotlin.math.max
import kotlin.math.min

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val address by viewModel.multicastAddress.collectAsState()
    val port by viewModel.port.collectAsState()
    val codecMode by viewModel.codecMode.collectAsState()
    val controlPort by viewModel.controlPort.collectAsState()
    val talkerID by viewModel.talkerId.collectAsState()
    //val groupId by viewModel.groupId.collectAsState()
    val jitterEnabled by viewModel.jitterBufferEnabled.collectAsState()
    val useIPv6 by viewModel.useIPv6.collectAsState()



    var addressError by remember { mutableStateOf(false) }
    var portError by remember { mutableStateOf(false) }
    var controlPortError by remember { mutableStateOf(false) }
    var talkerIDError by remember { mutableStateOf(false) }



    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )


        }



    }



@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodecModeDropdown(
    selectedMode: Int,
    onModeSelected: (Int) -> Unit,
    modes: List<Int>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },

        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = "$selectedMode bps",
            onValueChange = {},
            readOnly = true,
            label = { Text("CODEC2 Mode") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            modes.forEach { mode ->
                DropdownMenuItem(
                    text = { Text("$mode bps") },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    },
                    modifier = if (mode == selectedMode) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.fillMaxWidth()
                    }
                )
            }
        }
    }
}

fun isValidMulticastIP(ip: String): Boolean
{
    if (ip.isBlank()) return false

    return try
    {
        val addres = InetAddress.getByName(ip)
        addres.isMulticastAddress


    }
    catch (e: Exception) {
        false
    }


}


fun isValidPort(portStr: String): Boolean
{
    return try {
        val port = portStr.toInt()
        port in 5000..6000
    } catch (e: Exception) {
        false
    }
}

fun isValidTalkerID(idStr: String): Boolean {
    return try {
        val id = idStr.toInt()
        id in 1..500
    } catch (e: Exception) {
        false
    }
}
