package com.sznurczak.jf_inventur.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sznurczak.jf_inventur.R
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.Shoes
import com.sznurczak.jf_inventur.data.enums.Jugendflamme
import com.sznurczak.jf_inventur.data.enums.gender
import com.sznurczak.jf_inventur.ui.viewmodels.UserViewModel
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemberDetailScreen(
    memberId: Int,
    userViewModel: UserViewModel,
    onBack: () -> Unit
)
{
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val memberWithBadges by userViewModel.getMemberWithBadges(memberId)
        .collectAsState(initial = null)
    val shoes by userViewModel.getShoesForMember(memberId).collectAsState(initial = emptyList())

    var isEditMode by remember { mutableStateOf(false) }
    var editedMember by remember(memberWithBadges) {
        mutableStateOf(
            memberWithBadges?.member ?: MemberKind(
                firstName = "",
                lastName = "",
                gender = gender.MALE
            )
        )
    }

    // Sync editedMember with database changes after save
    LaunchedEffect(memberWithBadges) {
        val member = memberWithBadges?.member
        if (member != null && !isEditMode)
        {
            editedMember = member
        }
    }

    val photoPickerLauncher  = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            scope.launch {
                val storedUri = context.storeMemberProfilePicture(memberId, pickedUri)
                if (storedUri != null)
                {
                    editedMember = editedMember.copy(profilePictureUri = storedUri)
                }
            }
        }
    }

    var showAddBadgeDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zurück"
                    )
                }

                TextButton(
                    onClick = {
                        if (isEditMode)
                        {
                            userViewModel.updateMember(editedMember)
                        }
                        isEditMode = !isEditMode
                    }
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isEditMode) "Speichern" else "Bearbeiten")
                }
            }

            // Larger Profile Picture
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clickable(enabled = isEditMode)
                    {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = MaterialShapes.Cookie6Sided.toShape(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxSize(),
                    shadowElevation = 8.dp
                ) {
                    if (editedMember.profilePictureUri != null)
                    {
                        AsyncImage(
                            model = editedMember.profilePictureUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else
                    {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                            )
                        }
                    }
                }

                if (isEditMode)
                {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Name Fields (Stacked like contacts)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isEditMode)
                {
                    OutlinedTextField(
                        value = editedMember.firstName ?: "",
                        onValueChange = { editedMember = editedMember.copy(firstName = it) },
                        label = { Text("Vorname") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedMember.lastName ?: "",
                        onValueChange = { editedMember = editedMember.copy(lastName = it) },
                        label = { Text("Nachname") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else
                {
                    Text(
                        text = "${editedMember.firstName} ${editedMember.lastName}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.5f
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailItem(
                        icon = Icons.Default.Face,
                        label = "Geschlecht",
                        isEditMode = isEditMode
                    ) {
                        if (isEditMode)
                        {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                gender.entries.filter { it != gender.TOAST }.forEach { g ->
                                    FilterChip(
                                        selected = editedMember.gender == g,
                                        onClick = { editedMember = editedMember.copy(gender = g) },
                                        label = { Text(g.name.take(1)) }
                                    )
                                }
                            }
                        } else
                        {
                            Text(
                                editedMember.gender?.name ?: "Nicht angegeben",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    DetailItem(
                        icon = Icons.Default.Phone,
                        label = "Notfallkontakt",
                        isEditMode = isEditMode
                    ) {
                        if (isEditMode)
                        {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editedMember.emergencyContactName ?: "",
                                    onValueChange = {
                                        editedMember = editedMember.copy(emergencyContactName = it)
                                    },
                                    label = { Text("Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = editedMember.emergencyContactPhone ?: "",
                                        onValueChange = {
                                            editedMember =
                                                editedMember.copy(emergencyContactPhone = it)
                                        },
                                        label = { Text("Nummer") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        } else
                        {
                            Column {
                                Text(
                                    editedMember.emergencyContactName ?: "Kein Name",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (!editedMember.emergencyContactPhone.isNullOrBlank())
                                {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            val intent = Intent(
                                                Intent.ACTION_DIAL,
                                                "tel:${editedMember.emergencyContactPhone}".toUri()
                                            )
                                            context.startActivity(intent)
                                        }) {
                                        Text(
                                            editedMember.emergencyContactPhone ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Icon(
                                            Icons.Default.Call,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(start = 4.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Badges Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Abzeichen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Jugendflamme & Leistungsspange in a row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isEditMode)
                    {
                        Column {
                            Text("Jugendflamme", style = MaterialTheme.typography.labelSmall)
                            Row {
                                Jugendflamme.entries.forEach { level ->
                                    FilterChip(
                                        selected = editedMember.jugendflamme == level,
                                        onClick = {
                                            editedMember = editedMember.copy(jugendflamme = level)
                                        },
                                        label = {
                                            Text(
                                                if (level == Jugendflamme.NONE) "X" else level.name.last()
                                                    .toString()
                                            )
                                        }
                                    )
                                    Spacer(Modifier.width(4.dp))
                                }
                            }
                        }
                    } else
                    {
                        if (editedMember.jugendflamme != Jugendflamme.NONE)
                        {
                            AssistChip(
                                onClick = {},
                                label = { Text("Jugendflamme ${editedMember.jugendflamme?.name?.last()}") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Stars,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }

                    if (isEditMode)
                    {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = editedMember.hasLeistungsspange,
                                onCheckedChange = {
                                    editedMember = editedMember.copy(hasLeistungsspange = it)
                                })
                            Text("LS", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (editedMember.hasLeistungsspange)
                    {
                        AssistChip(
                            onClick = {},
                            label = { Text("Leistungsspange") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }

                // Custom Badges (Expressive Chips)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    memberWithBadges?.badges?.forEach { badge ->
                        InputChip(
                            selected = true,
                            onClick = {
                                if (isEditMode) userViewModel.removeBadgeFromMember(
                                    memberId,
                                    badge.id
                                )
                            },
                            label = { Text(badge.name) },
                            trailingIcon = {
                                if (isEditMode) Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomEnd = 0.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp
                            )
                        )
                    }
                    if (isEditMode)
                    {
                        AssistChip(
                            onClick = { showAddBadgeDialog = true },
                            label = { Text("Neu") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                        )
                    }
                }
            }

            // Equipment List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Ausrüstung (${shoes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (shoes.isEmpty())
                {
                    Text(
                        "Keine Teile verknüpft.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else
                {
                    shoes.forEach { shoe ->
                        EquipmentCard(shoe)
                    }
                }
            }
        }

        if (showAddBadgeDialog)
        {
            var newBadgeName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddBadgeDialog = false },
                title = { Text("Neues Abzeichen") },
                text = {
                    OutlinedTextField(
                        value = newBadgeName,
                        onValueChange = { newBadgeName = it },
                        label = { Text("Name") })
                },
                confirmButton = {
                    Button(onClick = {
                        if (newBadgeName.isNotBlank())
                        {
                            userViewModel.createAndAddBadge(memberId, newBadgeName)
                            showAddBadgeDialog = false
                        }
                    }) { Text("Hinzufügen") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddBadgeDialog = false }) { Text("Abbrechen") }
                }
            )
        }
    }
}

@Composable
fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isEditMode: Boolean,
    content: @Composable () -> Unit
)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = if (isEditMode) Alignment.Top else Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
        }
    }
}

@Composable
fun EquipmentCard(shoe: Shoes)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(
                    topStart = 15.dp,
                    bottomEnd = 15.dp,
                    topEnd = 4.dp,
                    bottomStart = 4.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("${shoe.brand} ${shoe.category}", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Größe: ${shoe.size} | Pair: ${shoe.pairId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private suspend fun Context.storeMemberProfilePicture(
    memberId: Int,
    sourceUri: Uri
): String? = withContext(Dispatchers.IO)
{
    runCatching {
        val profileDir = File(filesDir, "profile_pictures").apply { mkdirs() }
        val targetFile = File(profileDir, "member_${memberId}_profile_picture")

        contentResolver.openInputStream(sourceUri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return@withContext null

        Uri.fromFile(targetFile).toString()
    }.getOrNull()
}
