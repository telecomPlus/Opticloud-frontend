package com.example.opticloudtheempire.ui.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opticloudtheempire.data.model.Patient
import com.example.opticloudtheempire.ui.home.BottomNavigationBar

@Composable
fun PatientListScreen(
    viewModel: PatientsViewModel,
    onNavigateToNewPatient: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    // Estado para controlar qué paciente se está editando en el diálogo
    var patientToEdit by remember { mutableStateOf<Patient?>(null) }

    // Estado para controlar qué paciente se va a eliminar con confirmación
    var patientToDelete by remember { mutableStateOf<Patient?>(null) }

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewPatient,
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp),
                icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                text = { Text("Nuevo Paciente", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Directorio de Pacientes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Buscar por nombre o documento...", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (val state = listState) {
                is PatientsListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PatientsListState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchPatients() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                is PatientsListState.Success -> {
                    val filteredList = state.patients.filter {
                        it.name.contains(viewModel.searchQuery, ignoreCase = true) ||
                                it.documentNumber.contains(viewModel.searchQuery, ignoreCase = true) ||
                                it.phone.contains(viewModel.searchQuery, ignoreCase = true) ||
                                (it.address?.contains(viewModel.searchQuery, ignoreCase = true) == true)
                    }

                    if (filteredList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron pacientes.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(filteredList) { patient ->
                                PatientItemCard(
                                    patient = patient,
                                    onEditClick = { selected ->
                                        patientToEdit = selected
                                    },
                                    onDeleteClick = { selected ->
                                        patientToDelete = selected
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mostrar el modal únicamente cuando patientToEdit no sea nulo
        patientToEdit?.let { patient ->
            EditPatientDialog(
                patient = patient,
                onDismiss = { patientToEdit = null },
                onConfirm = { updatedPatient ->
                    patient._id?.let { id ->
                        viewModel.updatePatient(id, updatedPatient)
                    }
                    patientToEdit = null
                }
            )
        }

        // Modal de confirmación para eliminar paciente de forma segura
        patientToDelete?.let { patient ->
            AlertDialog(
                onDismissRequest = { patientToDelete = null },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )
                },
                title = {
                    Text(
                        text = "Eliminar Paciente",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar el expediente de \"${patient.name}\" (Doc: ${patient.documentNumber})? Esta acción no se puede deshacer y borrará su información permanentemente."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            patient._id?.let { id ->
                                viewModel.deletePatient(id)
                            }
                            patientToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { patientToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun PatientItemCard(
    patient: Patient,
    onEditClick: (Patient) -> Unit,
    onDeleteClick: (Patient) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Doc: ${patient.documentNumber}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = patient.phone,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { onEditClick(patient) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF1F5F9), shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { onDeleteClick(patient) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFEF2F2), shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditPatientDialog(
    patient: Patient,
    onDismiss: () -> Unit,
    onConfirm: (Patient) -> Unit
) {
    // Pre-cargamos los estados locales con la información existente del paciente
    var documentNumber by remember { mutableStateOf(patient.documentNumber) }
    var name by remember { mutableStateOf(patient.name) }
    var email by remember { mutableStateOf(patient.email) }
    var phone by remember { mutableStateOf(patient.phone) }
    var address by remember { mutableStateOf(patient.address ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Paciente", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = documentNumber,
                    onValueChange = { documentNumber = it },
                    label = { Text("Número de Identificación") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección (Opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = patient.copy(
                        documentNumber = documentNumber.trim(),
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        address = address.trim().ifEmpty { null }
                    )
                    onConfirm(updated)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}