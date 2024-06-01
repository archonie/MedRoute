package com.example.medroute.presentation.home_screen

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medroute.MainActivity
import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.presentation.common.MedRouteTopBar
import com.example.medroute.ui.theme.MedRouteTheme
import com.example.medroute.util.LocationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToRouteEntry: () -> Unit,
    navigateToRouteDetails: (Int) -> Unit,
    locationUtils: LocationUtils,
    logOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = user) {
        if(user == null){
            viewModel.getUser()
        }
    }


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {

            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Location Permission is required for this feature to work",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Location Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        })

    LaunchedEffect(key1 = locationUtils.hasLocationPermission(context)) {
        if (locationUtils.hasLocationPermission(context)) {

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MedRouteTopBar(
                title = "MedRoute",
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                isHomePage = true,
                onLogoutClick = {
                    coroutineScope.launch {
                        viewModel.logOut(user!!)
                    }
                    logOut()
                }
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (locationUtils.hasLocationPermission(context)) {
                        navigateToRouteEntry()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                contentColor = Color.White,
                containerColor = Color(0xFF7AB2B2),
                modifier = Modifier.padding(
                    end = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateEndPadding(LocalLayoutDirection.current)
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        })

    { paddingValues ->
        HomeBody(
            viewModel = viewModel,
            onRouteClick = navigateToRouteDetails,
            contentPadding = paddingValues,
            deleteRoute = viewModel::deleteRoute,

            )
    }
}


@Composable
fun HomeBody(
    viewModel: HomeViewModel,
    onRouteClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    deleteRoute: (Int, String) -> Unit,
    ) {
    val routes by viewModel.routes.collectAsState()
    var routeList by remember {
        mutableStateOf(emptyList<Route>())
    }
    var userMail by remember {
        mutableStateOf("")
    }
    val user by viewModel.user.collectAsState()

    LaunchedEffect(user) {
        user?.email?.let {
            userMail = it
        }

    }


    LaunchedEffect(routes) {
        viewModel.getRoutes(userMail)
        Log.d("RouteScreen", "Routes state updated: $routes")
        routes?.let { result ->
            if (result.isSuccess) {
                routeList = result.getOrNull() ?: emptyList()
                Log.d("RouteScreen", "Routes loaded successfully: $routeList")
            } else {
                routeList = emptyList()
                Log.e("RouteScreen", "Failed to load routes: ${result.exceptionOrNull()}")
            }
        }
    }

    if (routeList.isEmpty()) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "You have no routes yet!",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color(0xFF7AB2B2),
                    fontSize = 20.sp
                )
            )
            Text(
                "Click the + button to add a route.",
                style = MaterialTheme.typography.displaySmall.copy(
                    Color(0xFF7AB2B2),
                    fontSize = 20.sp
                )
            )
        }
    } else {
        user?.let {
            RouteList(
                onRouteClick = onRouteClick,
                contentPadding = contentPadding,
                deleteRoute = deleteRoute,
                user = it,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun RouteList(
    onRouteClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    deleteRoute: (Int, String) -> Unit,
    user: UserEntity,
    viewModel: HomeViewModel
) {
    val fetchedRoutes by viewModel.routes.collectAsState()
    val routes by viewModel.routeList.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getRoutes(user.email)
    }
    LaunchedEffect(fetchedRoutes) {
        viewModel.getRoutesFromDatabase()
    }
    LaunchedEffect(routes) {

    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(items = routes) { route ->
            var fetchedHospital by remember(route.hospitalId) { mutableStateOf<Hospital?>(null) }
            var fetchedHotel by remember(route.hotelId) { mutableStateOf<Hotel?>(null) }
            var isDataFetched by remember(route.hospitalId, route.hotelId) { mutableStateOf(false) }
            val hospitalState by viewModel.hospital.collectAsState()
            val hotelState by viewModel.hotel.collectAsState()


            LaunchedEffect(route.hospitalId, route.hotelId) {
                if (!isDataFetched) {
                    viewModel.getHospital(route.hospitalId)
                    viewModel.getHotel(route.hotelId)
                    isDataFetched = true
                }
            }



            hospitalState?.let {
                if (it.isSuccess && it.getOrNull()?.id == route.hospitalId) {
                    fetchedHospital = it.getOrNull()
                }
            }

            hotelState?.let {
                if (it.isSuccess && it.getOrNull()?.id == route.hotelId) {
                    fetchedHotel = it.getOrNull()
                }
            }

            if (fetchedHospital != null && fetchedHotel != null) {
                RouteListItem(
                    route = route,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onRouteClick(route.id) },
                    deleteRoute = deleteRoute,
                    hospital = fetchedHospital!!,
                    hotel = fetchedHotel!!,
                    user = user
                )
            } else {
                viewModel.getRoutes(user.email)
                viewModel.getRoutesFromDatabase()
                viewModel.getHotel(route.hotelId)
                viewModel.getHospital(route.hospitalId)
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium.copy(Color(0xFF7AB2B2)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun RouteListItem(
    route: Route,
    modifier: Modifier = Modifier,
    deleteRoute: (Int, String) -> Unit,
    user: UserEntity,
    hospital: Hospital,
    hotel: Hotel
) {


    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .height(120.dp)
            .border(
                BorderStroke(1.dp, color = Color(0xFF7AB2B2)),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(2.5f)) {
                    Text(
                        text = "Hospital: ${hospital.hospitalName}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF7AB2B2),
                            fontSize = 16.sp
                        ),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Hotel: ${hotel.hotelName} ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(
                                0xFF7AB2B2
                            ),
                            fontSize = 16.sp
                        )
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                deleteRoute(route.id, user.email)
                            },
                        imageVector = Icons.Default.Delete,
                        contentDescription = "DeleteRoute",
                        tint = Color(0xFF7AB2B2)
                    )
                }

            }
        }
    }
}




















