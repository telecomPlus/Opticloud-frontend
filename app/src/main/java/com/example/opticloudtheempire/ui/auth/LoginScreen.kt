package com.example.opticloudtheempire.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opticloudtheempire.data.local.TokenManager
import com.example.opticloudtheempire.data.model.LoginRequest
import com.example.opticloudtheempire.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- LOGO PROFESIONAL OPTICLOUD ---
            OptiCloudLogo()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "OptiCloud",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold, // <--- Corregido
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Sistema de Gestión Óptica",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor completa todos los campos"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.instance.login(
                                LoginRequest(email = email.trim(), password = password)
                            )

                            if (response.success && response.token != null) {
                                tokenManager.saveToken(response.token)
                                onLoginSuccess()
                            } else {
                                errorMessage = response.message ?: "Credenciales inválidas"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error de conexión: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar Sesión", fontSize = 16.sp)
                }
            }
        }
    }
}

/**
 * Componente visual que dibuja un logotipo moderno y profesional (Gafas + Nube)
 */
@Composable
fun OptiCloudLogo() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier.size(90.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Fondo circular con gradiente suave
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.15f), secondaryColor.copy(alpha = 0.25f))
                ),
                radius = canvasWidth / 2f
            )

            // Lente izquierdo de las gafas
            drawArc(
                color = primaryColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(canvasWidth * 0.12f, canvasHeight * 0.35f),
                size = Size(canvasWidth * 0.32f, canvasWidth * 0.32f),
                style = Stroke(width = 8f)
            )

            // Lente derecho de las gafas
            drawArc(
                color = primaryColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(canvasWidth * 0.56f, canvasHeight * 0.35f),
                size = Size(canvasWidth * 0.32f, canvasWidth * 0.32f),
                style = Stroke(width = 8f)
            )

            // Puente de unión de las gafas
            drawLine(
                color = primaryColor,
                start = Offset(canvasWidth * 0.44f, canvasHeight * 0.45f),
                end = Offset(canvasWidth * 0.56f, canvasHeight * 0.45f),
                strokeWidth = 8f
            )

            // Detalle superior estilo Nube tecnológica
            drawArc(
                color = secondaryColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(canvasWidth * 0.3f, canvasHeight * 0.15f),
                size = Size(canvasWidth * 0.4f, canvasWidth * 0.25f)
            )
        }
    }
}