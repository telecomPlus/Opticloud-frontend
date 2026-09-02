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
                                        // Abre el diálogo asignando el paciente a editar
                                        patientToEdit = selected
                                    },
                                    onDeleteClick = { selected ->
                                        // Aquí implementas tu borrado si lo requieres
                                        viewModel.deletePatient(selected._id)
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
                    // Llamada al ViewModel con el ID y el objeto modificado para Node.js
                    // (Asegúrate de cambiar 'patient.id' o 'patient._id' según tu modelo data class)
                    viewModel.updatePatient(patient._id, updatedPatient)
                    patientToEdit = null
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
                    text = "ID: ${patient.address ?: "Sin Doc."}",
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
    var name by remember { mutableStateOf(patient.name) }
    var address by remember { mutableStateOf(patient.address ?: "") }
    var phone by remember { mutableStateOf(patient.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Paciente", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Documento / Dirección") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    val updated = patient.copy(
                        name = name,
                        address = address,
                        phone = phone
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