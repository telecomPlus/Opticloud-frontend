package com.example.opticloudtheempire.ui.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // <--- IMPORT CRÍTICO
import androidx.compose.runtime.getValue         // <--- IMPORT CRÍTICO
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NewPatientScreen(
    viewModel: PatientsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    // Si en PatientsViewModel definiste "listState", usa listState.
    // Si mantienes "uiState", asegúrate de que la propiedad exista en el ViewModel.
    val uiState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Nuevo Paciente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    Button(
                        onClick = { viewModel.savePatient(onSuccess = onBackClick) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        enabled = uiState !is PatientsListState.Loading
                    ) {
                        if (uiState is PatientsListState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Paciente", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.fullName,
                onValueChange = { viewModel.fullName = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.address,
                onValueChange = { viewModel.address = it },
                label = { Text("Documento de Identidad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = { viewModel.phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            if (uiState is PatientsListState.Error) {
                Text(
                    text = (uiState as PatientsListState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }
    }
}