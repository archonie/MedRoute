package com.example.medroute.presentation.route_entry

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.presentation.common.MedRouteTopBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteEntryScreen(
    modifier: Modifier = Modifier,
    viewModel: RouteEntryViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        MedRouteTopBar(
            title = "Create Route",
            canNavigateBack = true,
            scrollBehavior = scrollBehavior,
            navigateUp = navigateUp
        )
    }) { paddingValues ->
        RouteEntryBody(
            modifier = Modifier
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection = LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(layoutDirection = LocalLayoutDirection.current),
                    top = paddingValues.calculateTopPadding()
                )
                .fillMaxSize(),
            viewModel = viewModel,
            navigateUp = navigateUp
        )
    }
}

@Composable
fun RouteEntryBody(
    modifier: Modifier = Modifier,
    viewModel: RouteEntryViewModel,
    onHotelSearchClick: (Hotel) -> Unit = {},
    onHospitalSearchClick: (Hospital) -> Unit = {},
    navigateUp: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()

    val currentLocation by viewModel.currentLocation

    var hospitalName by remember { mutableStateOf("") }

    val hospitalSuggestions by viewModel.hospitalSuggestions.collectAsState()

    var chosenHospital by remember { mutableStateOf<Hospital?>(null) }

    var showHospitalSuggestions by remember { mutableStateOf(false) }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val hotelSuggestions by viewModel.hotelSuggestions.collectAsState()
    var hotelName by remember { mutableStateOf("") }
    var chosenHotel by remember { mutableStateOf<Hotel?>(null) }
    var showHotelSuggestions by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val routePostSuccess by viewModel.routePostSuccess.collectAsState()

    val user by viewModel.user.collectAsState()

    fun updateHotelSuggestions(query: String) {
        viewModel.searchHotelSuggestions(query)
    }

    fun updateHospitalSuggestions(query: String) {
        viewModel.searchHospitalSuggestions(query)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentLocation()
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            coroutineScope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitude, it.longitude), 15f
                    )
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    val newLocation = Location("").apply {
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                    }
                    viewModel.updateLocation(newLocation)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLng(latLng)
                        )
                    }
                }
            ) {
                currentLocation?.let {
                    Marker(
                        state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                        title = "Current Location"
                    )
                }
            }
        }

        val isClicked = interactionSource.collectIsPressedAsState().value
        LaunchedEffect(key1 = isClicked) {
            if (isClicked) {
                onClick?.invoke()
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Slider(
                value = cameraPositionState.position.zoom, onValueChange = { newZoom ->
                    cameraPositionState.move(
                        CameraUpdateFactory.zoomTo(newZoom)
                    )
                }, colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF7AB2B2),
                    activeTrackColor = Color(0xFF7AB2B2),
                    inactiveTrackColor = Color(0xFFEEF7FF)
                ), valueRange = 10f..20f, modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            )

            OutlinedTextField(
                value = hotelName,
                onValueChange = {
                    hotelName = it
                    updateHotelSuggestions(hotelName)
                    showHotelSuggestions = true
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEEF7FF),
                    unfocusedContainerColor = Color(0xFFEEF7FF),
                    disabledContainerColor = Color(0xFFEEF7FF),
                    disabledBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color(0xA67AB2B2)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xA67AB2B2)),
                label = {
                    Text(
                        text = "Hotel Name",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xA67AB2B2))
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.searchHotelSuggestions(hotelName)
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            )

            if (showHotelSuggestions) {
                hotelSuggestions?.let { result ->
                    if (result.isSuccess) {
                        result.getOrNull()?.let { hotels ->
                            Log.d("Parsed Hotels", hotels.toString())
                            LazyColumn(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                items(hotels) { hotel ->
                                    Log.d("Id", hotel.id.toString())
                                    Text(
                                        text = hotel.hotelName,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                chosenHotel = hotel
                                                hotelName = hotel.hotelName
                                                showHotelSuggestions = false
                                                focusManager.clearFocus()
                                                keyboardController?.hide()
                                            },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF7AB2B2)
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        Log.e("HotelSuggestions", "Failed to fetch hotels: ${result.exceptionOrNull()?.message}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = hospitalName,
                onValueChange = {
                    hospitalName = it
                    updateHospitalSuggestions(hospitalName)
                    showHospitalSuggestions = true
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEEF7FF),
                    unfocusedContainerColor = Color(0xFFEEF7FF),
                    disabledContainerColor = Color(0xFFEEF7FF),
                    disabledBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color(0xA67AB2B2)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xA67AB2B2)),
                label = {
                    Text(
                        text = "Hospital Name",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xA67AB2B2))
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.searchHospitalSuggestions(hospitalName)
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            )

            if (showHospitalSuggestions) {
                hospitalSuggestions?.let { result ->
                    if (result.isSuccess) {
                        result.getOrNull()?.let {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                items(it) { hospital ->
                                    Text(
                                        text = hospital.hospitalName, modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                chosenHospital = hospital
                                                hospitalName = hospital.hospitalName
                                                showHospitalSuggestions = false
                                                focusManager.clearFocus()
                                                keyboardController?.hide()
                                                Log.d("Id", hospital.id.toString())
                                            }, style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color(0xFF7AB2B2)
                                            ))
                                }
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(18.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7AB2B2),
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (chosenHotel != null && chosenHospital != null && user != null) {
                        viewModel.insertRoute(
                            chosenHotel!!,
                            chosenHospital!!,
                            userMail = user!!.email
                        )
                        navigateUp()
                    } else {
                        Log.e("SaveButton", "Hotel, Hospital or User is null")
                    }
                },
                enabled = hotelName.isNotEmpty() && hospitalName.isNotEmpty(),
            ) {
                Text(
                    text = "Save Route",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7AB2B2),
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = navigateUp,
            ) {
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
        }

    }
}
