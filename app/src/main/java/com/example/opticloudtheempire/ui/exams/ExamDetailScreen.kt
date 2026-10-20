package com.example.opticloudtheempire.ui.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.opticloudtheempire.data.model.ExamResponseDto
import com.example.opticloudtheempire.data.model.EyeGraduationDto
import com.example.opticloudtheempire.data.model.VisualMeasurementsDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    examId: String,
    viewModel: ExamsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(examId) {
        viewModel.fetchExamDetail(examId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Examen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
                actions = {
                    IconButton(onClick = { viewModel.fetchExamDetail(examId) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            when (val state = detailState) {
                is ExamDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2563EB))
                    }
                }

                is ExamDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchExamDetail(examId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is ExamDetailUiState.Success -> {
                    val exam = state.exam
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // TARJETA DE RESUMEN DEL PACIENTE Y EXAMEN
                        ExamHeaderCard(exam = exam)

                        // TABLA DE GRADUACIÓN COMPARATIVA OD VS OI
                        GraduationComparisonCard(od = exam.od, os = exam.os)

                        // MEDICIONES ADICIONALES
                        if (exam.visualMeasurements != null) {
                            MeasurementsCard(measurements = exam.visualMeasurements)
                        }

                        // DIAGNÓSTICO Y RECOMENDACIONES
                        ClinicalSummaryCard(exam = exam)

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExamHeaderCard(exam: ExamResponseDto) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exam.getPatientName(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                    exam.getPatientDocument()?.let { doc ->
                        Text(
                            text = "Doc: $doc",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                AssistChip(
                    onClick = {},
                    label = { Text(exam.getFormattedExamType(), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFEFF6FF),
                        labelColor = Color(0xFF1E40AF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Fecha del examen", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = exam.examDate?.take(10) ?: exam.createdAt?.take(10) ?: "Fecha no disponible",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                val optometrist = exam.getOptometristName()
                if (optometrist.isNotBlank()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Profesional a cargo", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = optometrist,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (!exam.reasonForVisit.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Motivo de consulta:", fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = exam.reasonForVisit,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            }
        }
    }
}

@Composable
fun GraduationComparisonCard(od: EyeGraduationDto?, os: EyeGraduationDto?) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Prescripción Óptica",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Encabezado de la tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Parámetro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "OD (Derecho)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "OI (Izquierdo)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Filas clínicas
            DetailRow("Esfera (SPH)", formatDiopters(od?.sphere), formatDiopters(os?.sphere))
            DetailRow("Cilindro (CYL)", formatDiopters(od?.cylinder), formatDiopters(os?.cylinder))
            DetailRow("Eje (AXIS)", formatAxis(od?.axis), formatAxis(os?.axis))
            DetailRow("Adición (ADD)", formatDiopters(od?.addition), formatDiopters(os?.addition))
            DetailRow("Prisma", formatNumber(od?.prism), formatNumber(os?.prism))
            DetailRow("Base Prisma", od?.prismBase ?: "—", os?.prismBase ?: "—")
            DetailRow("AV Lejana", od?.visualAcuityDistance ?: "—", os?.visualAcuityDistance ?: "—")
            DetailRow("AV Próxima", od?.visualAcuityNear ?: "—", os?.visualAcuityNear ?: "—")
        }
    }
}

@Composable
fun DetailRow(label: String, valueOD: String, valueOS: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = valueOD,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (valueOD == "—") Color.LightGray else Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valueOS,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (valueOS == "—") Color.LightGray else Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )
    }
    HorizontalDivider(color = Color(0xFFF8FAFC))
}

@Composable
fun MeasurementsCard(measurements: VisualMeasurementsDto) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mediciones Visuales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MeasurementItem("DP Binocular", formatMm(measurements.pupillaryDistance))
                MeasurementItem("Dist. Trabajo", formatCm(measurements.workingDistance))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MeasurementItem("DP OD / OI", "${formatMm(measurements.rightPupillaryDistance)} / ${formatMm(measurements.leftPupillaryDistance)}")
                MeasurementItem("Presión OD / OI", "${formatMmHg(measurements.intraocularPressureOD)} / ${formatMmHg(measurements.intraocularPressureOS)}")
            }
        }
    }
}

@Composable
fun MeasurementItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF0F172A))
    }
}

@Composable
fun ClinicalSummaryCard(exam: ExamResponseDto) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Evaluación Clínica",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E293B)
            )

            if (!exam.diagnosis.isNullOrBlank()) {
                ClinicalSection("Diagnóstico / Impresión Clínica", exam.diagnosis)
            }

            if (!exam.recommendations.isNullOrBlank()) {
                ClinicalSection("Recomendaciones / Tratamiento", exam.recommendations)
            }

            if (!exam.notes.isNullOrBlank()) {
                ClinicalSection("Observaciones Internas", exam.notes)
            }

            if (!exam.followUpDate.isNullOrBlank()) {
                ClinicalSection("Fecha sugerida de control", exam.followUpDate.take(10))
            }
        }
    }
}

@Composable
fun ClinicalSection(title: String, content: String) {
    Column {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF2563EB))
        Spacer(modifier = Modifier.height(2.dp))
        Text(content, fontSize = 13.sp, color = Color(0xFF334155))
    }
}

fun formatDiopters(value: Double?): String {
    if (value == null) return "—"
    val sign = if (value > 0) "+" else ""
    return "$sign${String.format("%.2f", value)}"
}

fun formatAxis(value: Int?): String {
    if (value == null) return "—"
    return "$value°"
}

fun formatNumber(value: Double?): String {
    if (value == null) return "—"
    return String.format("%.2f", value)
}

fun formatMm(value: Double?): String {
    if (value == null) return "—"
    return "${String.format("%.1f", value)} mm"
}

fun formatCm(value: Double?): String {
    if (value == null) return "—"
    return "${String.format("%.1f", value)} cm"
}

fun formatMmHg(value: Double?): String {
    if (value == null) return "—"
    return "${String.format("%.0f", value)} mmHg"
}
