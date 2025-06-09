package com.example.citymap.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.citymap.data.model.Coord
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    coord: Coord,
    modifier: Modifier = Modifier
) {
    val cityLatLng = LatLng(coord.lat, coord.lon)
    val markerState = remember { MarkerState(position = cityLatLng) }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(cityLatLng) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(cityLatLng, 10f)
        )
        markerState.position = cityLatLng
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = markerState,
            title = "Location",
            snippet = "Lat: ${coord.lat}, Lon: ${coord.lon}"
        )
    }
}