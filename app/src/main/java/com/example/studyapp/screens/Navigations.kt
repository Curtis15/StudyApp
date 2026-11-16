package com.example.studyapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyapp.viewmodel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") {LoginScreen(viewModel, navController)}
        composable("home") {HomeScreen(viewModel, navController)}
        composable("sessions") {AllSessionScreen(viewModel, navController)}
        composable("create") {CreateSessionScreen(viewModel, navController)}
        composable(
            "details/{sessionId}",
            arguments = listOf(navArgument("sessionId") {type = NavType.StringType})
        ) { entry ->
            val id = entry.arguments?.getString("sessionId") ?: ""
            DetailsScreen(id, viewModel, navController)
        }
    }
}
