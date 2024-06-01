package com.example.medroute.presentation.route_details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.domain.model.Route
import com.example.medroute.presentation.common.MedRouteTopBar
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    viewModel: RouteDetailsViewModel = viewModel(),
    routeId: Int,
    navigateUp: () -> Unit,
    navigateToLandmark: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route by viewModel.route.collectAsState()
    LaunchedEffect(Unit) {
        if (route == null) {
            viewModel.getRoute(routeId)
        }
    }
    if (route != null) {
        Scaffold(
            topBar = {
                MedRouteTopBar(
                    title = "Route Details",
                    canNavigateBack = true,
                    navigateUp = navigateUp,
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            RouteDetailsBody(
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection = LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(layoutDirection = LocalLayoutDirection.current),
                        top = paddingValues.calculateTopPadding()
                    )
                    .fillMaxSize(),
                viewModel = viewModel,
                route = route!!,
                navigateToLandmark = navigateToLandmark
            )
        }
    }


}

@Composable
fun RouteDetailsBody(
    route: Route,
    viewModel: RouteDetailsViewModel,
    modifier: Modifier = Modifier,
    navigateToLandmark: (Int) -> Unit
) {

    val hospital by viewModel.hospital.collectAsState()
    val hotel by viewModel.hotel.collectAsState()
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
    val cameraPositionState = rememberCameraPositionState()
    val hospitalLocationResult by viewModel.hospitalLocation.observeAsState()
    val hotelLocationResult by viewModel.hotelLocation.observeAsState()
    val landmarkLocationResult by viewModel.landmarkLocation.observeAsState()
    val landmarkSuggestions by viewModel.landmarkSuggestions.collectAsState()

    var chosenLandmarks by remember {
        mutableStateOf(emptyList<Landmark>())
    }
    var landmarkLocations by remember {
        mutableStateOf(emptyList<Location>())
    }

    var hotelLocation by remember {
        mutableStateOf<Location?>(null)
    }
    LaunchedEffect(Unit) {
        viewModel.getHospital(route.hospitalId)
        viewModel.getHotel(route.hotelId)
    }
    LaunchedEffect(hospital, hotel) {
        hospital?.let {
            if (it.isSuccess) {
                val hospital2 = it.getOrNull()
                if (hospital2 != null) {
                    viewModel.getHospitalLocation(hospital2.locationId)
                }
            }
        }
        hotel?.let {
            if (it.isSuccess) {
                val hotel2 = it.getOrNull()
                if (hotel2 != null) {
                    viewModel.getHotelLocation(hotel2.locationId)
                }
            }
        }
    }

    hotelLocationResult?.let { result ->
        result.fold(
            onSuccess = { location ->
                hotelLocation = location
            },
            onFailure = { exception ->
                // Handle failure
            }
        )
    }

    LaunchedEffect(hotelLocation) {
        hotelLocation?.let {
            viewModel.getLandmarkSuggestions(it.latitude, it.longitude, 20.0, 20)
            Log.d("landmarks", "landmarks fetched")
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude), 12f
            )
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        GoogleMap(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            properties = mapProperties,
            uiSettings = mapUiSettings.copy(zoomControlsEnabled = false),
            cameraPositionState = cameraPositionState,
        ) {
            hospitalLocationResult?.let { result ->
                result.fold(
                    onSuccess = { location ->
                        Marker(
                            state = MarkerState(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                )
                            ),
                            title = "Your Hospital",
                            snippet = hospital?.getOrNull()?.hospitalName
                        )
                    },
                    onFailure = { exception ->
                        // Handle failure
                    }
                )
            }

            hotelLocationResult?.let { result ->
                result.fold(
                    onSuccess = { location ->
                        Marker(
                            state = MarkerState(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                )
                            ),
                            title = "Your Hotel",
                            snippet = hotel?.getOrNull()?.hotelName
                        )
                    },
                    onFailure = { exception ->
                        // Handle failure
                    }
                )
            }


        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Slider(
                value = cameraPositionState.position.zoom, onValueChange = { newZoom ->
                    viewModel.getLandmarkSuggestions(hotelLocation!!.latitude, hotelLocation!!.longitude, 21.0-(newZoom.toDouble()),20)
                    cameraPositionState.move(
                        CameraUpdateFactory.zoomTo(newZoom)
                    )
                }, colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF7AB2B2),
                    activeTrackColor = Color(0xFF7AB2B2),
                    inactiveTrackColor = Color(0xFFEEF7FF)
                ), valueRange = 9f..20f, modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            )
            landmarkSuggestions?.let { result ->
                if (result.isSuccess) {
                    result.getOrNull()?.let { landmarks ->
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(landmarks) { landmark ->
                                LandmarkListItem(
                                    viewModel = viewModel,
                                    landmark = landmark,
                                    modifier = Modifier.clickable { navigateToLandmark(landmark.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LandmarkListItem(
    viewModel: RouteDetailsViewModel,
    landmark: Landmark,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val landmarkLocations by viewModel.landmarkLocations.observeAsState(emptyMap())
    val locationResult = landmarkLocations[landmark.locationId]

    // Fetch the landmark location when the composable is first composed
    LaunchedEffect(landmark.locationId) {
        viewModel.getLandmarkLocation(landmark.locationId)
    }

    var location by remember { mutableStateOf<Location?>(null) }
    var url by remember { mutableStateOf("") }

    // Update location when landmarkLocation changes
    LaunchedEffect(locationResult) {
        locationResult?.fold(
            onSuccess = {
                location = it
                url = it.url
                Log.d("landmark", "$location")
                Log.d("url", url)
            },
            onFailure = {
                Log.e("landmark", "Error fetching landmark location", it)
            }
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .height(120.dp)
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, color = Color(0xFF7AB2B2)),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        location?.let { loc ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(loc.url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)),
            )
        } ?: run {
            // Placeholder for when the image is not available yet
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF7AB2B2))
            )
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = landmark.landmarkName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF7AB2B2)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = landmark.landmarkInfo,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF7AB2B2), fontSize = 12.sp
                )
            )
        }
    }
}