package com.example.medroute.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.medroute.presentation.home_screen.HomeScreen
import com.example.medroute.presentation.home_screen.HomeViewModel
import com.example.medroute.presentation.landmark_screen.LandmarkScreen
import com.example.medroute.presentation.landmark_screen.LandmarkViewModel
import com.example.medroute.presentation.login_screen.LoginScreen
import com.example.medroute.presentation.login_screen.LoginViewModel
import com.example.medroute.presentation.route_details.RouteDetailsScreen
import com.example.medroute.presentation.route_details.RouteDetailsViewModel
import com.example.medroute.presentation.route_entry.RouteEntryScreen
import com.example.medroute.presentation.signup_screen.SignUpScreen
import com.example.medroute.presentation.signup_screen.SignUpViewModel
import com.example.medroute.util.LocationUtils

@Composable
fun MedRouteNavHost(
    startDestination: String,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = startDestination) {
        navigation(
            route = NavigationDestination.AppStartDestination.route,
            startDestination = NavigationDestination.LoginDestination.route
        ) {
            composable(
                route = NavigationDestination.LoginDestination.route
            ) {
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = viewModel,
                    onSignUpClick = { navController.navigate(NavigationDestination.SignUpDestination.route) },
                    navigateToHome = { navController.navigate(NavigationDestination.HomeDestination.route) }
                )
            }
            composable(route = NavigationDestination.SignUpDestination.route) {
                val viewModel: SignUpViewModel = hiltViewModel()

                SignUpScreen(
                    viewModel = viewModel, onNavigateUp = { navController.navigateUp() }
                )
            }
        }
        navigation(
            route = NavigationDestination.UserFoundDestination.route,
            startDestination = NavigationDestination.HomeDestination.route
        ) {
            composable(
                route = NavigationDestination.HomeDestination.route
            ) {
                val viewModel: HomeViewModel = hiltViewModel()
                val locationUtils = LocationUtils(context = context)
                HomeScreen(
                    locationUtils = locationUtils,
                    viewModel = viewModel,
                    navigateToRouteEntry = { navController.navigate(NavigationDestination.RouteEntryDestination.route) },
                    navigateToRouteDetails = { routeId ->
                        navigateToRouteDetails(
                            navController = navController, routeId = routeId
                        )
                    },
                    logOut = { navController.navigate(NavigationDestination.LoginDestination.route) }
                )
            }

            composable(
                route = NavigationDestination.RouteEntryDestination.route
            ) {
                RouteEntryScreen(navigateUp = { navController.navigateUp() })
            }
            composable(
                route = NavigationDestination.RouteDetailsDestination.route
            ) {
                val viewModel: RouteDetailsViewModel = hiltViewModel()
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("route")
                    ?.let { routeId ->
                        RouteDetailsScreen(
                            viewModel = viewModel,
                            routeId = routeId,
                            navigateUp = { navController.navigateUp() },
                            navigateToLandmark = { id ->
                                navigateToLandmark(
                                    landmarkId = id,
                                    navController = navController
                                )
                            }
                        )
                    }
            }
            composable(
                route = NavigationDestination.LandmarkDestination.route
            ) {
                val viewModel: LandmarkViewModel = hiltViewModel()
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("landmark")
                    ?.let { landmarkId ->
                        LandmarkScreen(
                            viewModel = viewModel,
                            landmarkId = landmarkId,
                            navigateUp = { navController.navigateUp() }
                        )
                    }
            }
        }

    }
}

private fun navigateToRouteDetails(navController: NavController, routeId: Int) {
    navController.currentBackStackEntry?.savedStateHandle?.set("route", routeId)
    navController.navigate(
        route = NavigationDestination.RouteDetailsDestination.route
    )
}

private fun navigateToLandmark(navController: NavController, landmarkId: Int) {
    navController.currentBackStackEntry?.savedStateHandle?.set("landmark", landmarkId)
    navController.navigate(
        route = NavigationDestination.LandmarkDestination.route
    )
}
