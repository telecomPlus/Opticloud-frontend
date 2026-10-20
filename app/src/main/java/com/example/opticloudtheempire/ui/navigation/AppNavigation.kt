package com.example.opticloudtheempire.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.opticloudtheempire.data.local.TokenManager
import com.example.opticloudtheempire.ui.auth.LoginScreen
import com.example.opticloudtheempire.ui.exams.ExamDetailScreen
import com.example.opticloudtheempire.ui.exams.ExamHistoryScreen
import com.example.opticloudtheempire.ui.exams.ExamScreen
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
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Principal (Home)
        composable("home") {
            HomeScreen(
                onNavigateToPatients = {
                    navController.navigate("patients") {
                        launchSingleTop = true
                    }
                },
                onNavigateToExams = { navController.navigate("new_exam") },
                onNavigateToInventory = { navController.navigate("inventory") }
            )
        }

        // Listado de Pacientes
        composable("patients") {
            PatientListScreen(
                viewModel = patientsViewModel,
                onNavigateToNewPatient = { navController.navigate("new_patient") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToNewExam = { patientId ->
                    navController.navigate("new_exam/$patientId")
                },
                onNavigateToExamHistory = { patientId ->
                    navController.navigate("exam_history/$patientId")
                }
            )
        }

        // Crear Nuevo Paciente
        composable("new_patient") {
            NewPatientScreen(
                viewModel = patientsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Alias para ruta general de exámenes
        composable("exams") {
            ExamScreen(
                patientId = null,
                onBackClick = { navController.popBackStack() },
                onNavigateToDetail = { examId ->
                    navController.navigate("exam_detail/$examId")
                },
                onNavigateToHistory = { patientId ->
                    navController.navigate("exam_history/$patientId")
                }
            )
        }

        // Registrar Nuevo Examen (Sin paciente preseleccionado)
        composable("new_exam") {
            ExamScreen(
                patientId = null,
                onBackClick = { navController.popBackStack() },
                onNavigateToDetail = { examId ->
                    navController.navigate("exam_detail/$examId")
                },
                onNavigateToHistory = { patientId ->
                    navController.navigate("exam_history/$patientId")
                }
            )
        }

        // Registrar Nuevo Examen (Con paciente preseleccionado)
        composable(
            route = "new_exam/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientIdArg = backStackEntry.arguments?.getString("patientId")
            ExamScreen(
                patientId = patientIdArg,
                onBackClick = { navController.popBackStack() },
                onNavigateToDetail = { examId ->
                    navController.navigate("exam_detail/$examId")
                },
                onNavigateToHistory = { patientId ->
                    navController.navigate("exam_history/$patientId")
                }
            )
        }

        // Historial Clínico de Exámenes por Paciente
        composable(
            route = "exam_history/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientIdArg = backStackEntry.arguments?.getString("patientId") ?: ""
            ExamHistoryScreen(
                patientId = patientIdArg,
                onBackClick = { navController.popBackStack() },
                onNavigateToNewExam = { pId ->
                    navController.navigate("new_exam/$pId")
                },
                onNavigateToDetail = { examId ->
                    navController.navigate("exam_detail/$examId")
                }
            )
        }

        // Detalle de un Examen Optométrico
        composable(
            route = "exam_detail/{examId}",
            arguments = listOf(navArgument("examId") { type = NavType.StringType })
        ) { backStackEntry ->
            val examIdArg = backStackEntry.arguments?.getString("examId") ?: ""
            ExamDetailScreen(
                examId = examIdArg,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}