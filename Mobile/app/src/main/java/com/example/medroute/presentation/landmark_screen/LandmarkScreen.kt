package com.example.medroute.presentation.landmark_screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.presentation.common.MedRouteTopBar
import com.example.medroute.presentation.route_details.RouteDetailsBody
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkScreen(
    viewModel: LandmarkViewModel,
    landmarkId: Int,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val landmark by viewModel.landmark.observeAsState()
    var selectedLandmark by remember { mutableStateOf<Landmark?>(null) }

    LaunchedEffect(landmarkId) {
        viewModel.getLandmark(landmarkId)
    }

    LaunchedEffect(landmark) {
        landmark?.let {
            if (it.isSuccess) {
                selectedLandmark = it.getOrNull()
            }
        }
    }

    Scaffold(
        topBar = {
            MedRouteTopBar(
                title = "Landmark Details",
                canNavigateBack = true,
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        selectedLandmark?.let { landmark ->
            LandmarkBody(
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection = LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(layoutDirection = LocalLayoutDirection.current),
                        top = paddingValues.calculateTopPadding()
                    )
                    .fillMaxSize(),
                viewModel = viewModel,
                landmark = landmark,
            )
        } ?: run {
            // Handle the loading or empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection = LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(layoutDirection = LocalLayoutDirection.current),
                        top = paddingValues.calculateTopPadding()
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun LandmarkBody(
    modifier: Modifier = Modifier,
    viewModel: LandmarkViewModel,
    landmark: Landmark
) {
    val context = LocalContext.current
    val landmarkLocation by viewModel.landmarkLocation.observeAsState()
    var location by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
    val cameraPositionState = rememberCameraPositionState()

    // Use rememberUpdatedState to keep track of landmark.id changes without recomposition
    val currentLandmarkId by rememberUpdatedState(landmark.id)
    var url by remember {
        mutableStateOf("")
    }
    LaunchedEffect(currentLandmarkId) {
        viewModel.getLocation(landmark.locationId)
    }

    LaunchedEffect(landmarkLocation) {
        landmarkLocation?.let {
            it.fold(
                onSuccess = { loc ->
                    location = loc
                    isLoading = false
                },
                onFailure = {
                    isLoading = false
                }
            )
        }
    }
    LaunchedEffect(key1 = url) {
        viewModel.getLocation(landmark.locationId)
    }
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (location != null) {
            url = location!!.url
            Log.d("url", url)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(location!!.latitude, location!!.longitude), 15f
            )
            GoogleMap(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(LatLng(location!!.latitude, location!!.longitude)),
                    title = landmark.landmarkName
                )
            }
        } else if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(context = context).data(url).build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            Text(text = landmark.landmarkName,
                style = MaterialTheme.typography.displayMedium.copy(color = Color(0xFF7AB2B2)), )
            Text(text = landmark.landmarkInfo, style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF7AB2B2), fontSize = 16.sp
            ))
        }
    }
}

