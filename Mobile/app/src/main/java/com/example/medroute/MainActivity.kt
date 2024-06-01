package com.example.medroute

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.medroute.navigation.MedRouteNavHost
import com.example.medroute.navigation.NavigationDestination
import com.example.medroute.presentation.home_screen.HomeScreen
import com.example.medroute.presentation.login_screen.LoginScreen
import com.example.medroute.presentation.signup_screen.SignUpScreen
import com.example.medroute.ui.theme.MedRouteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MedRouteTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val user by viewModel.user.collectAsState()
                var startDestination by remember {
                    mutableStateOf("")
                }
                if (user != null) {
                    startDestination = NavigationDestination.UserFoundDestination.route
                } else {
                    startDestination = NavigationDestination.AppStartDestination.route
                }

                MedRouteNavHost(startDestination = startDestination)
            }
        }

    }
}
