package com.sznurczak.jf_inventur.ui.screens

import android.R.attr.label
import android.provider.Settings.Global.getString
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.sznurczak.jf_inventur.data.database.Shoes
import com.sznurczak.jf_inventur.data.database.ShoeSide
import com.sznurczak.jf_inventur.ui.viewmodels.InventoryViewModel
import com.sznurczak.jf_inventur.ui.viewmodels.UserViewModel
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sznurczak.jf_inventur.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.sznurczak.jf_inventur.data.database.ShoeWithMember
import com.sznurczak.jf_inventur.data.tag.ScanResult
import com.sznurczak.jf_inventur.ui.viewmodels.SettingsViewModel
import com.sznurczak.jf_inventur.ui.viewmodels.ScanUiState
import com.sznurczak.jf_inventur.ui.viewmodels.ScanViewModel
import com.sznurczak.jf_inventur.data.enums.abzeichen
import com.sznurczak.jf_inventur.data.enums.gender



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    settingviewModel: SettingsViewModel,
    scanViewModel: ScanViewModel,
    inventoryViewModel: InventoryViewModel,
    userViewModel: UserViewModel,
    onClear: () -> Unit
) {
    val scanState by scanViewModel.uiState.collectAsStateWithLifecycle()
    val members by userViewModel.membersWithShoes.collectAsState()
    
    var showRegistration by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Reset registration view when scan state changes
    LaunchedEffect(scanState) {
        if (scanState !is ScanUiState.UnknownTag) {
            showRegistration = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (val state = scanState) {
            is ScanUiState.FoundItem -> {
                ScanResultCard(
                    shoeWithMember = state.data, 
                    onClear = onClear,
                    onEdit = { showEditSheet = true }
                )
                
                if (showEditSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showEditSheet = false },
                        sheetState = sheetState
                    ) {
                        QuickRegisterForm(
                            tagId = state.data.shoe.id,
                            initialShoe = state.data.shoe,
                            members = members.map { it.member },
                            onSave = { updatedShoe ->
                                inventoryViewModel.registerNewShoe(
                                    id = updatedShoe.id,
                                    pairId = updatedShoe.pairId,
                                    side = updatedShoe.side,
                                    size = updatedShoe.size,
                                    brand = updatedShoe.brand,
                                    category = updatedShoe.category,
                                    date = updatedShoe.dateOfPurchase,
                                    memberId = updatedShoe.memberId
                                )
                                showEditSheet = false
                                // Refresh current scan view
                                scanViewModel.onScan(ScanResult.SimpleId(updatedShoe.id))
                            },
                            onCancel = { showEditSheet = false },
                            onAddMember = { first, last, g, a ->
                                userViewModel.addMember(first, last, g, a)
                            }
                        )
                    }
                }
            }
            is ScanUiState.UnknownTag -> {
                if (!showRegistration) {
                    UnknownTagCard(tagId = state.tagId, onRegister = { showRegistration = true }, onClear = onClear)
                } else {
                    QuickRegisterForm(
                        tagId = state.tagId,
                        members = members.map { it.member },
                        onSave = { shoe ->
                            inventoryViewModel.registerNewShoe(
                                id = shoe.id,
                                pairId = shoe.pairId,
                                side = shoe.side,
                                size = shoe.size,
                                brand = shoe.brand,
                                category = shoe.category,
                                date = shoe.dateOfPurchase,
                                memberId = shoe.memberId
                            )
                            onClear() // This will trigger a re-scan or go to Idle
                        },
                        onCancel = { showRegistration = false },
                        onAddMember = { first, last, g, a ->
                            userViewModel.addMember(first, last, g, a)
                        }
                    )
                }
            }
            is ScanUiState.Error -> {
                Text("Fehler: ${state.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = onClear) { Text("Zurück") }
            }
            ScanUiState.Scanning -> CircularProgressIndicator()
            ScanUiState.Idle -> {
                Text("Bereit zum Scannen...", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun UnknownTagCard(tagId: String, onRegister: () -> Unit, onClear: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Unbekannter Tag", style = MaterialTheme.typography.headlineSmall)
            Text("ID: $tagId", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Diesen Tag registrieren")
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Abbrechen")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickRegisterForm(
    tagId: String,
    initialShoe: Shoes? = null,
    members: List<com.sznurczak.jf_inventur.data.database.MemberKind>,
    onSave: (Shoes) -> Unit,
    onCancel: () -> Unit,
    onAddMember: (String, String, gender, abzeichen) -> Unit
) {
    var brand by remember { mutableStateOf(initialShoe?.brand ?: "") }
    var size by remember { mutableStateOf(initialShoe?.size?.toString() ?: "42") }
    var side by remember { mutableStateOf(initialShoe?.side ?: ShoeSide.LEFT) }
    var selectedMemberId by remember { mutableStateOf<Int?>(initialShoe?.memberId) }
    var expanded by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = if (initialShoe != null) CardDefaults.cardElevation(0.dp) else CardDefaults.cardElevation(4.dp),
        colors = if (initialShoe != null) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (initialShoe != null) "Ausrüstung bearbeiten" else "Neuen Schuh anlegen",
                style = MaterialTheme.typography.titleLarge
            )
            //Zeigt Tag ID klein an
            Text("Tag ID: $tagId", style = MaterialTheme.typography.bodySmall)
            
            OutlinedTextField(value = brand, onValueChange = { brand = it.trimEnd() }, label = { Text("Marke") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = size,
                onValueChange = { if(it.isDigitsOnly()){ size = it} },
                label = { Text("Größe") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = side == ShoeSide.LEFT, onClick = { side = ShoeSide.LEFT }, label = { Text("Links") })
                FilterChip(selected = side == ShoeSide.RIGHT, onClick = { side = ShoeSide.RIGHT }, label = { Text("Rechts") })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Nutzer Liste aus Datenbank
                ExposedDropdownMenuBox(
                    expanded = expanded, 
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = members.find { it.uid == selectedMemberId }
                            ?.let { "${it.firstName} ${it.lastName}" } ?: "Kein Besitzer",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Besitzer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Kein Besitzer") },
                            onClick = { selectedMemberId = null; expanded = false })
                        members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text("${member.firstName} ${member.lastName}") },
                                onClick = { selectedMemberId = member.uid; expanded = false }
                            )
                        }
                    }
                }
                IconButton(onClick = { showAddMemberDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        //contentDescription = "Neues Mitglied hinzufügen",
                        contentDescription = stringResource(R.string.create_new_user),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showAddMemberDialog) {
                AddMemberDialog(
                    onDismiss = { showAddMemberDialog = false },
                    onConfirm = { first, last, g, a ->
                        onAddMember(first, last, g, a)
                        showAddMemberDialog = false
                    }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f))
                {
                    Text("Abbrechen")
                }
                Button(onClick =
                    {
                    onSave(Shoes(
                        id = tagId,
                        pairId = initialShoe?.pairId ?: "PAIR_${System.currentTimeMillis()}",
                        side = side,
                        size = size.toIntOrNull() ?: 0,
                        brand = brand,
                        category = initialShoe?.category ?: "Stiefel",
                        dateOfPurchase = initialShoe?.dateOfPurchase ?: "2024-01-01",
                        memberId = selectedMemberId
                    ))
                }, modifier = Modifier.weight(1f)) { 
                    Text(if (initialShoe != null) "Aktualisieren" else "Speichern") 
                }
            }
        }
    }
}









@Composable
fun ScanCard(
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
    shoeWithMember: ShoeWithMember,
    onClear: () -> Unit,
    onEdit: () -> Unit, // Added this
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
            // Header: Icon and Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Bearbeiten", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Icon(
                imageVector = Icons.Default.Info,
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
                ResultRow(label = "ID", value = shoeWithMember.shoe.id)

                val ownerName = if (shoeWithMember.member != null) {
                    "${shoeWithMember.member.firstName ?: ""} ${shoeWithMember.member.lastName ?: ""}".trim()
                } else {
                    "Nicht zugewiesen"
                }
                
                ResultRow(label = "Besitzer", value = ownerName)
                ResultRow(label = "Größe", value = shoeWithMember.shoe.size.toString())
                ResultRow(label = "Seite", value = shoeWithMember.shoe.side.name)
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

