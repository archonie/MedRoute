package com.example.medroute.navigation

sealed interface NavigationDestination {
    val route: String

    object AppStartDestination: NavigationDestination{
        override val route: String = "appStart"
    }

    object UserFoundDestination: NavigationDestination{
        override val route: String = "userFound"
    }
    object LoginDestination: NavigationDestination{
        override val route: String = "loginScreen"
    }

    object SignUpDestination: NavigationDestination{
        override val route: String = "signUpScreen"
    }
    object HomeDestination: NavigationDestination{
        override val route: String = "homeScreen"
    }

    object RouteEntryDestination: NavigationDestination{
        override val route: String = "routeEntry"
    }
    object RouteDetailsDestination: NavigationDestination{
        override val route: String = "routeDetails"
    }
    object PlaceholderDestination: NavigationDestination{
        override val route: String = "placeHolder"
    }
    object LandmarkDestination: NavigationDestination{
        override val route: String = "landmark"
    }

}