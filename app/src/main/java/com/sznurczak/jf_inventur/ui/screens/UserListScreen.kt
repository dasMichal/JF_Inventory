package com.sznurczak.jf_inventur.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.unit.dp
import com.sznurczak.jf_inventur.R
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.Shoes
import com.sznurczak.jf_inventur.data.enums.abzeichen
import com.sznurczak.jf_inventur.data.enums.gender
import com.sznurczak.jf_inventur.ui.viewmodels.InventoryViewModel
import com.sznurczak.jf_inventur.ui.viewmodels.UserViewModel


@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    InventoryViewModel: InventoryViewModel,
    UserViewModel: UserViewModel
) {
    val membersWithShoes by UserViewModel.membersWithShoes.collectAsState()
    var showAddMemberDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Mitgliederverwaltung",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(membersWithShoes) { item ->
                    MemberCard(
                        member = item.member,
                        shoeCount = item.shoes.size,
                        onDelete = { UserViewModel.deleteMember(item.member) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddMemberDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Mitglied hinzufügen")
        }

        if (showAddMemberDialog) {
            AddMemberDialog(
                onDismiss = { showAddMemberDialog = false },
                onConfirm = { first, last, g, a ->
                    UserViewModel.addMember(first, last, g, a)
                    showAddMemberDialog = false
                }
            )
        }
    }
}

@Composable
fun MemberCard(member: MemberKind, shoeCount: Int, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f),

            )
            {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("${member.firstName} ${member.lastName}", style = MaterialTheme.typography.titleMedium)
                    Text("Ausrüstung: $shoeCount Teile", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, gender, abzeichen) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(gender.MALE) } // Assuming MALE exists
    var selectedAbzeichen by remember { mutableStateOf(abzeichen.NONE) } // Assuming NONE exists

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neues Mitglied") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it.trimEnd() }, label = { Text("Vorname") })
                OutlinedTextField(value = lastName, onValueChange = { lastName = it.trimEnd() }, label = { Text("Nachname") })
                // Simple gender toggle
                Row {
                    FilterChip(selected = selectedGender == gender.MALE, onClick = { selectedGender = gender.MALE }, label = { Text("M") })
                    Spacer(Modifier.width(4.dp))
                    FilterChip(selected = selectedGender == gender.FEMALE, onClick = { selectedGender = gender.FEMALE }, label = { Text("W") })
                    Spacer(Modifier.width(4.dp))
                    FilterChip(selected = selectedGender == gender.NON_BINARY, onClick = { selectedGender = gender.NON_BINARY }, label = { Text("X") })
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(firstName, lastName, selectedGender, selectedAbzeichen) }) {
                Text("Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}














