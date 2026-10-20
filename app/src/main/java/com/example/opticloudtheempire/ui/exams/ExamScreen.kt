package com.example.opticloudtheempire.ui.exams

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.opticloudtheempire.data.model.ExamResponseDto
import com.example.opticloudtheempire.data.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    patientId: String? = null,
    viewModel: ExamsViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHistory: (String) -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    var showPatientPickerDialog by remember { mutableStateOf(false) }
    var patientSearchQuery by remember { mutableStateOf("") }
    var isMeasurementsExpanded by remember { mutableStateOf(false) }

    // Preseleccionar paciente si se recibió por parámetro de navegación
    LaunchedEffect(patientId) {
        if (!patientId.isNullOrBlank()) {
            viewModel.selectPatientById(patientId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar Examen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveExam() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        enabled = formState !is ExamFormUiState.Submitting
                    ) {
                        if (formState is ExamFormUiState.Submitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guardar Examen",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- ALERTA DE ERROR GENERAL ---
            if (formState is ExamFormUiState.Error) {
                val errorState = formState as ExamFormUiState.Error
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorState.message,
                            color = Color(0xFF991B1B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ==========================================
            // SECCIÓN 1: PACIENTE ASOCIADO
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Paciente Asociado *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val patient = viewModel.selectedPatient
                    if (patient != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = patient.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Doc: ${patient.documentNumber} • Tel: ${patient.phone}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            TextButton(onClick = { showPatientPickerDialog = true }) {
                                Text("Cambiar", color = Color(0xFF2563EB))
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showPatientPickerDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2563EB)
                            )
                        ) {
                            Icon(Icons.Default.PersonSearch, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscar y Seleccionar Paciente")
                        }
                    }

                    if (viewModel.fieldErrors.containsKey("patientId")) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.fieldErrors["patientId"] ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ==========================================
            // SECCIÓN 2: DATOS GENERALES
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Datos Generales",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tipo de examen:",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val examTypes = listOf(
                        "examen_visual" to "Examen visual",
                        "control" to "Control",
                        "lentes_contacto" to "Lentes de contacto",
                        "urgencia" to "Urgencia"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        examTypes.forEach { (typeKey, label) ->
                            FilterChip(
                                selected = viewModel.examType == typeKey,
                                onClick = { viewModel.examType = typeKey },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = viewModel.reasonForVisit,
                        onValueChange = { viewModel.reasonForVisit = it },
                        label = { Text("Motivo de consulta") },
                        placeholder = { Text("Ej: Visión borrosa de lejos, fatiga visual") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // ==========================================
            // SECCIÓN 3: GRADUACIÓN ÓPTICA (OD vs OI)
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Graduación Óptica",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = Color(0xFFDBEAFE)) {
                                Text("OD = Derecho", color = Color(0xFF1E40AF), fontSize = 10.sp, modifier = Modifier.padding(3.dp))
                            }
                            Badge(containerColor = Color(0xFFF3E8FF)) {
                                Text("OI = Izquierdo", color = Color(0xFF6B21A8), fontSize = 10.sp, modifier = Modifier.padding(3.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Encabezados de columnas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("Ojo Derecho (OD)", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), fontSize = 13.sp)
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("Ojo Izquierdo (OI)", fontWeight = FontWeight.Bold, color = Color(0xFF9333EA), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fila 1: Esfera (SPH)
                    ClinicalInputRow(
                        title = "Esfera (SPH)",
                        valueOD = viewModel.odSphere,
                        onValueChangeOD = { viewModel.odSphere = it },
                        valueOS = viewModel.osSphere,
                        onValueChangeOS = { viewModel.osSphere = it },
                        placeholder = "Ej: -2.00",
                        keyboardType = KeyboardType.Text
                    )

                    // Fila 2: Cilindro (CYL)
                    ClinicalInputRow(
                        title = "Cilindro (CYL)",
                        valueOD = viewModel.odCylinder,
                        onValueChangeOD = { viewModel.odCylinder = it },
                        valueOS = viewModel.osCylinder,
                        onValueChangeOS = { viewModel.osCylinder = it },
                        placeholder = "Ej: -0.75",
                        keyboardType = KeyboardType.Text
                    )

                    // Fila 3: Eje (AXIS) - Obligatorio si Cilindro != 0
                    ClinicalInputRow(
                        title = "Eje (1° a 180°)",
                        valueOD = viewModel.odAxis,
                        onValueChangeOD = { viewModel.odAxis = it },
                        valueOS = viewModel.osAxis,
                        onValueChangeOS = { viewModel.osAxis = it },
                        placeholder = "Ej: 180",
                        keyboardType = KeyboardType.Number,
                        errorOD = viewModel.fieldErrors["od.axis"],
                        errorOS = viewModel.fieldErrors["os.axis"]
                    )

                    // Fila 4: Adición (ADD)
                    ClinicalInputRow(
                        title = "Adición (ADD)",
                        valueOD = viewModel.odAddition,
                        onValueChangeOD = { viewModel.odAddition = it },
                        valueOS = viewModel.osAddition,
                        onValueChangeOS = { viewModel.osAddition = it },
                        placeholder = "Ej: +1.50",
                        keyboardType = KeyboardType.Text
                    )

                    // Fila 5: Prisma
                    ClinicalInputRow(
                        title = "Prisma",
                        valueOD = viewModel.odPrism,
                        onValueChangeOD = { viewModel.odPrism = it },
                        valueOS = viewModel.osPrism,
                        onValueChangeOS = { viewModel.osPrism = it },
                        placeholder = "Ej: 1.0",
                        keyboardType = KeyboardType.Text
                    )

                    // Fila 6: Base del prisma
                    ClinicalInputRow(
                        title = "Base Prisma",
                        valueOD = viewModel.odPrismBase,
                        onValueChangeOD = { viewModel.odPrismBase = it },
                        valueOS = viewModel.osPrismBase,
                        onValueChangeOS = { viewModel.osPrismBase = it },
                        placeholder = "Ej: Base Superior",
                        keyboardType = KeyboardType.Text
                    )

                    // Fila 7: Agudeza Visual Lejana
                    ClinicalInputRow(
                        title = "AV Lejana",
                        valueOD = viewModel.odVisualAcuityDistance,
                        onValueChangeOD = { viewModel.odVisualAcuityDistance = it },
                        valueOS = viewModel.osVisualAcuityDistance,
                        onValueChangeOS = { viewModel.osVisualAcuityDistance = it },
                        placeholder = "Ej: 20/20",
                        keyboardType = KeyboardType.Text
                    )

                    // Fila 8: Agudeza Visual Próxima
                    ClinicalInputRow(
                        title = "AV Próxima",
                        valueOD = viewModel.odVisualAcuityNear,
                        onValueChangeOD = { viewModel.odVisualAcuityNear = it },
                        valueOS = viewModel.osVisualAcuityNear,
                        onValueChangeOS = { viewModel.osVisualAcuityNear = it },
                        placeholder = "Ej: 20/20",
                        keyboardType = KeyboardType.Text
                    )
                }
            }

            // ==========================================
            // SECCIÓN 4: MEDICIONES ADICIONALES (EXPANDIBLE)
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isMeasurementsExpanded = !isMeasurementsExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                tint = Color(0xFF2563EB)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Mediciones Visuales Adicionales (Opcional)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                        Icon(
                            imageVector = if (isMeasurementsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    AnimatedVisibility(visible = isMeasurementsExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = viewModel.pupillaryDistance,
                                onValueChange = { viewModel.pupillaryDistance = it },
                                label = { Text("Distancia Pupilar Binocular (DP en mm)") },
                                placeholder = { Text("Ej: 62.5") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = viewModel.rightPupillaryDistance,
                                    onValueChange = { viewModel.rightPupillaryDistance = it },
                                    label = { Text("DP Monocular OD (mm)") },
                                    placeholder = { Text("Ej: 31.0") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = viewModel.leftPupillaryDistance,
                                    onValueChange = { viewModel.leftPupillaryDistance = it },
                                    label = { Text("DP Monocular OI (mm)") },
                                    placeholder = { Text("Ej: 31.5") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = viewModel.intraocularPressureOD,
                                    onValueChange = { viewModel.intraocularPressureOD = it },
                                    label = { Text("Presión OD (PIO mmHg)") },
                                    placeholder = { Text("Ej: 14") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = viewModel.intraocularPressureOS,
                                    onValueChange = { viewModel.intraocularPressureOS = it },
                                    label = { Text("Presión OI (PIO mmHg)") },
                                    placeholder = { Text("Ej: 15") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = viewModel.workingDistance,
                                onValueChange = { viewModel.workingDistance = it },
                                label = { Text("Distancia de Trabajo (cm)") },
                                placeholder = { Text("Ej: 40") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // ==========================================
            // SECCIÓN 5: INFORMACIÓN CLÍNICA
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Diagnóstico y Recomendaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )

                    OutlinedTextField(
                        value = viewModel.diagnosis,
                        onValueChange = { viewModel.diagnosis = it },
                        label = { Text("Diagnóstico o impresión clínica") },
                        placeholder = { Text("Ej: Astigmatismo miópico compuesto OD y OI") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.recommendations,
                        onValueChange = { viewModel.recommendations = it },
                        label = { Text("Recomendaciones / Prescripción") },
                        placeholder = { Text("Ej: Uso permanente de lentes con filtro azul y antireflejo") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.notes,
                        onValueChange = { viewModel.notes = it },
                        label = { Text("Observaciones adicionales") },
                        placeholder = { Text("Notas internas del examen optométrico") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.followUpDate,
                        onValueChange = { viewModel.followUpDate = it },
                        label = { Text("Fecha sugerida de control (AAAA-MM-DD)") },
                        placeholder = { Text("Ej: 2027-10-20") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ==========================================
    // MODAL DE CONFIRMACIÓN DE ÉXITO
    // ==========================================
    if (formState is ExamFormUiState.Success) {
        val successState = formState as ExamFormUiState.Success
        val createdExam = successState.exam

        AlertDialog(
            onDismissRequest = { /* No cerrar haciendo tap afuera para obligar acción */ },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(36.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¡Examen Registrado con Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = successState.message,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Paciente: ${viewModel.selectedPatient?.name ?: createdExam.getPatientName()}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "OD: ${createdExam.od?.toSummaryString() ?: "Registrado"}",
                        fontSize = 12.sp,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "OI: ${createdExam.os?.toSummaryString() ?: "Registrado"}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B21A8)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val patientToView = viewModel.selectedPatient?._id ?: ""
                        viewModel.resetForm()
                        if (patientToView.isNotBlank()) {
                            onNavigateToHistory(patientToView)
                        } else {
                            onNavigateToDetail(createdExam._id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Ver Historial")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.resetForm()
                        onNavigateToDetail(createdExam._id)
                    }
                ) {
                    Text("Ver Detalle")
                }
            }
        )
    }

    // ==========================================
    // DIÁLOGO SELECTOR DE PACIENTES
    // ==========================================
    if (showPatientPickerDialog) {
        Dialog(onDismissRequest = { showPatientPickerDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Seleccionar Paciente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { showPatientPickerDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = patientSearchQuery,
                        onValueChange = { patientSearchQuery = it },
                        placeholder = { Text("Buscar por nombre o cédula...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredPatients = viewModel.availablePatients.filter {
                        it.name.contains(patientSearchQuery, ignoreCase = true) ||
                                it.documentNumber.contains(patientSearchQuery, ignoreCase = true)
                    }

                    if (filteredPatients.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron pacientes.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredPatients) { p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.onSelectPatient(p)
                                            showPatientPickerDialog = false
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = p.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Doc: ${p.documentNumber} • Tel: ${p.phone}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila visual de dos columnas para comparar mediciones de OD y OI
 */
@Composable
fun ClinicalInputRow(
    title: String,
    valueOD: String,
    onValueChangeOD: (String) -> Unit,
    valueOS: String,
    onValueChangeOS: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    errorOD: String? = null,
    errorOS: String? = null
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = valueOD,
                    onValueChange = onValueChangeOD,
                    placeholder = { Text(placeholder, fontSize = 12.sp, color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    isError = errorOD != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                if (errorOD != null) {
                    Text(
                        text = errorOD,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = valueOS,
                    onValueChange = onValueChangeOS,
                    placeholder = { Text(placeholder, fontSize = 12.sp, color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    isError = errorOS != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9333EA),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                if (errorOS != null) {
                    Text(
                        text = errorOS,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}
