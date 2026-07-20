package com.sznurczak.jf_inventur.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.enums.Jugendflamme
import com.sznurczak.jf_inventur.data.enums.gender
import com.sznurczak.jf_inventur.ui.viewmodels.UserViewModel

/**
 * Main screen for displaying the list of members.
 * Allows adding new members and navigating to member details.
 *
 * @param modifier Modifier for the root layout.
 * @param userViewModel ViewModel providing member data and actions.
 * @param onMemberClick Callback when a member is clicked for editing.
 */
@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onMemberClick: (Int) -> Unit
) {
    val membersWithShoes by userViewModel.membersWithShoes.collectAsState()
    var showAddMemberDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(membersWithShoes) { item ->
                    var isExpanded by remember { mutableStateOf(false) }
                    MemberCard(
                        member = item.member,
                        shoeCount = item.shoes.size,
                        isExpanded = isExpanded,
                        onExpandChange = { isExpanded = it },
                        onDelete = { userViewModel.deleteMember(item.member) },
                        onEdit = { onMemberClick(item.member.uid) }
                    )
                }
            }
        }

        // Floating Action Button to add a new member
        FloatingActionButton(
            onClick = { showAddMemberDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Mitglied hinzufügen")
        }

        // Dialog for adding a new member
        if (showAddMemberDialog) {
            AddMemberDialog(
                onDismiss = { showAddMemberDialog = false },
                onConfirm = { first, last, g ->
                    userViewModel.addMember(first, last, g)
                    showAddMemberDialog = false
                }
            )
        }
    }
}

/**
 * A card representing a single member with their basic info and equipment count.
 * Can be expanded to show more details and actions.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemberCard(
    member: MemberKind,
    shoeCount: Int,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Infinite transition for the rotating border effect
    val infiniteTransition = rememberInfiniteTransition(label = "border_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing)
        ),
        label = "rotation_value"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Profile, Name, and Expansion/Edit actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Profile Image with rotating border
                    //val avatarSize = if (isExpanded) 100.dp else 60.dp
                    val avatarSize by animateDpAsState(
                        targetValue = if (isExpanded) 90.dp else 60.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "avatar_size"
                    )
                    val iconSize by animateDpAsState(
                        targetValue = if (isExpanded) 40.dp else 24.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "icon_size"
                    )
                    val outerShape = MaterialShapes.Cookie6Sided.toShape()

                    Box(
                        modifier = Modifier.size(avatarSize),
                        contentAlignment = Alignment.Center
                    ) {

                        // Actual image content, clipped and not rotating
                        Surface(
                            shape = outerShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(1.dp)
                                //.clip(outerShape)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = outerShape
                                )
//                                .border(
//                                    width = 2.dp,
//                                    brush = Brush.sweepGradient(
//                                        colors = listOf(
//                                            MaterialTheme.colorScheme.primary,
//                                            MaterialTheme.colorScheme.secondary,
//                                            MaterialTheme.colorScheme.primary
//                                        )
//                                    ),
//                                    shape = outerShape
                                //))
                        ) {
                            if (!member.profilePictureUri.isNullOrEmpty()) {
                                AsyncImage(
                                    model = member.profilePictureUri,
                                    contentDescription = "Profilbild",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(iconSize),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }


                    Spacer(Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "${member.firstName ?: ""} ${member.lastName ?: ""}".trim(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Ausrüstung: $shoeCount Teile",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action buttons in header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Bearbeiten",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onExpandChange(!isExpanded) }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Weniger anzeigen" else "Mehr anzeigen"
                        )
                    }
                }
            }

            // Expanded Details Section
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Emergency Contact Info
                    if (!member.emergencyContactPhone.isNullOrBlank()) {
                        val context = LocalContext.current
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()

                                .padding(vertical = 4.dp)
                        ) {



                            Button(
                                    onClick = {
                                        val intent = Intent(
                                            Intent.ACTION_DIAL,
                                            "tel:${member.emergencyContactPhone}".toUri()
                                        )
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),

                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Anrufen",
                                    modifier = Modifier
                                        .size(20.dp),
//                                        .clickable {
//                                            val intent = Intent(
//                                                Intent.ACTION_DIAL,
//                                                "tel:${member.emergencyContactPhone}".toUri()
//                                            )
//                                            context.startActivity(intent)
//                                        },

                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "Notfallkontakt anrufen",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }

                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = member.emergencyContactName ?: "Notfallkontakt ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = member.emergencyContactPhone,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    // Chips for Badges/Qualifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (member.jugendflamme != null && member.jugendflamme != Jugendflamme.NONE) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("JF ${member.jugendflamme.name.last()}") },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        if (member.hasLeistungsspange) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Leistungsspange") },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Delete Action (moved here from header)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Mitglied löschen")
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Mitglied löschen?") },
            text = { Text("Möchtest du ${member.firstName} ${member.lastName} wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Abbrechen") }
            }
        )
    }
}

/**
 * Dialog for adding a new member with basic details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, gender) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(gender.MALE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neues Mitglied hinzufügen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Vorname") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nachname") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Text(text = "Geschlecht", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedGender == gender.MALE,
                        onClick = { selectedGender = gender.MALE },
                        label = { Text("Männlich") }
                    )
                    FilterChip(
                        selected = selectedGender == gender.FEMALE,
                        onClick = { selectedGender = gender.FEMALE },
                        label = { Text("Weiblich") }
                    )
                    FilterChip(
                        selected = selectedGender == gender.NON_BINARY,
                        onClick = { selectedGender = gender.NON_BINARY },
                        label = { Text("Divers") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(firstName.trim(), lastName.trim(), selectedGender) },
                enabled = firstName.isNotBlank() && lastName.isNotBlank()
            ) {
                Text("Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun PreviewMemberCardCollapsed() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MemberCard(
                member = MemberKind(
                    firstName = "Max",
                    lastName = "Mustermann",
                    gender = gender.MALE,
                    jugendflamme = Jugendflamme.LEVEL_1,
                    hasLeistungsspange = true,
                    emergencyContactPhone = "0123456789"
                ),
                shoeCount = 3,
                isExpanded = false,
                onExpandChange = {},
                onDelete = {},
                onEdit = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMemberCardExpanded() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MemberCard(
                member = MemberKind(
                    firstName = "Erika",
                    lastName = "Musterfrau",
                    gender = gender.FEMALE,
                    jugendflamme = Jugendflamme.LEVEL_2,
                    hasLeistungsspange = false,
                    emergencyContactName = "Max Mustermann",
                    emergencyContactPhone = "0987654321"
                ),
                shoeCount = 5,
                isExpanded = true,
                onExpandChange = {},
                onDelete = {},
                onEdit = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddMemberDialog() {
    MaterialTheme {
        AddMemberDialog(onDismiss = {}, onConfirm = { _, _, _ -> })
    }
}
