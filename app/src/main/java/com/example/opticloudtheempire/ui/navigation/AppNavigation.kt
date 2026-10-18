package com.example.opticloudtheempire.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.opticloudtheempire.data.local.TokenManager
import com.example.opticloudtheempire.ui.auth.LoginScreen
import com.example.opticloudtheempire.ui.home.HomeScreen
import com.example.opticloudtheempire.ui.patients.NewPatientScreen
import com.example.opticloudtheempire.ui.patients.PatientListScreen
import com.example.opticloudtheempire.ui.patients.PatientsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val patientsViewModel: PatientsViewModel = viewModel()
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    // Si ya existe un token guardado, iniciamos en "home", de lo contrario en "login"
    val startDestination = if (tokenManager.getToken() != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        
        // Pantalla de Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Al iniciar sesión con éxito, navegamos a home y limpiamos el historial para que no pueda volver atrás con el botón físico
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Principal (Home)
        composable("home") {
            HomeScreen(
                onNavigateToPatients = { navController.navigate("patients") },
                onNavigateToExams = { navController.navigate("exams") },
                onNavigateToInventory = { navController.navigate("inventory") }
            )
        }

        // Listado de Pacientes
        composable("patients") {
            PatientListScreen(
                viewModel = patientsViewModel,
                onNavigateToNewPatient = { navController.navigate("new_patient") },
                onNavigateToHome = { navController.navigate("home") }
            )
        }

        // Crear Nuevo Paciente
        composable("new_patient") {
            NewPatientScreen(
                viewModel = patientsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}