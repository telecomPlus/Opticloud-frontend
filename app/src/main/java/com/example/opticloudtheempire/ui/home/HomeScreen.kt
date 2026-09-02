package com.example.opticloudtheempire.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.opticloudtheempire.data.model.InventoryAlert

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToPatients: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(32.dp))
            QuickAccessSection(onNavigateToPatients, onNavigateToExams, onNavigateToInventory)
            Spacer(modifier = Modifier.height(32.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    InventoryAlertsCard(alerts = state.alerts)
                }
                is HomeUiState.Error -> {
                    // Fallback visual demostrativo en caso de que el backend no devuelva datos o el endpoint no exista aún
                    val demoAlerts = listOf(
                        InventoryAlert("1", "Lentes Bifocales", 3),
                        InventoryAlert("2", "Marcos Ray-Ban Original", 2)
                    )
                    InventoryAlertsCard(alerts = demoAlerts)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Error de red: ${state.message}", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DG",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Médico Optometrista", fontSize = 14.sp, color = Color.Gray)
                Text("Hola, Dr. Murcia", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        IconButton(onClick = { /* Acción de Logout */ }) {
            Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
        }
    }
}

@Composable
fun QuickAccessSection(
    onNavigateToPatients: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    Text("ACCESOS RÁPIDOS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickAccessCard("Pacientes", Icons.Default.People, onNavigateToPatients)
        QuickAccessCard("Exámenes", Icons.Default.Visibility, onNavigateToExams)
        QuickAccessCard("Inventario", Icons.Default.Inventory, onNavigateToInventory)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAccessCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(105.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun InventoryAlertsCard(alerts: List<InventoryAlert>) {
    Text("ALERTAS DE INVENTARIO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)) // Fondo suave rojo
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Bajo Stock Crítico", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (alerts.isEmpty()) {
                Text("No hay alertas críticas.", fontSize = 14.sp)
            } else {
                alerts.forEach { alert ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(alert.itemName, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFD32F2F).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${alert.remainingQuantity} uds",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Inicio", "Pacientes", "Productos", "Perfil")
    val icons = listOf(Icons.Default.Home, Icons.Default.People, Icons.Default.Category, Icons.Default.AccountCircle)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}