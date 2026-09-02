package com.example.opticloudtheempire.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.opticloudtheempire.ui.home.HomeScreen
import com.example.opticloudtheempire.ui.patients.NewPatientScreen
import com.example.opticloudtheempire.ui.patients.PatientListScreen
import com.example.opticloudtheempire.ui.patients.PatientsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val patientsViewModel: PatientsViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToPatients = { navController.navigate("patients") },
                onNavigateToExams = { navController.navigate("exams") },
                onNavigateToInventory = { navController.navigate("inventory") }
            )
        }
        composable("patients") {
            PatientListScreen(
                viewModel = patientsViewModel,
                onNavigateToNewPatient = { navController.navigate("new_patient") },
                onNavigateToHome = { navController.navigate("home") }
            )
        }
        composable("new_patient") {
            NewPatientScreen(
                viewModel = patientsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}