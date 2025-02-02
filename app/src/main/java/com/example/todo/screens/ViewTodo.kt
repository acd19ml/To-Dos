package com.example.todo.screens

import android.graphics.Bitmap
import androidx.compose.material3.Icon
import com.example.todo.viewmodels.TodosViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.todo.entities.TodoEntity
import com.example.todo.services.GeoLocationService.locationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ViewTodo(
    viewModel: TodosViewModel,
    todo: TodoEntity
) {
    // Displays detailed information about a specific task, including title, description,
    // priority, tags, location, and an image (if available)
    // Calculates the distance to the task location if location permissions are granted

    val imageBitmap = todo.imageBytes?.let { byteArrayToBitmap(it) }
    val tags = viewModel.getTagsForTodo(todo.id.toLong())
    val tagsAsState by tags.collectAsState(initial = emptyList())
    var calculatedDistance by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(todo) {
        val lat = locationViewModel?.latitude ?: 0.0
        val lon = locationViewModel?.longitude ?: 0.0
        val todoLat = todo.latitude ?: 0.0
        val todoLon = todo.longitude ?: 0.0
        calculatedDistance = locationViewModel?.calculateDistance(lat, lon, todoLat, todoLon)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // This section renders the image associated with the task as a banner
        BannerImageDisplay(imageBitmap)

        // This section shows detailed information about the task
        Column(modifier = Modifier.padding(16.dp)) {
            Text(todo.title, style = MaterialTheme.typography.headlineMedium)
            Divider(Modifier.padding(vertical = 8.dp))

            Text("Priority: ${todo.priority.name}", style = MaterialTheme.typography.bodyLarge)
            Divider(Modifier.padding(vertical = 8.dp))

            Text(todo.description, style = MaterialTheme.typography.bodyMedium)
            Divider(Modifier.padding(vertical = 8.dp))

            Text(
                "To Complete By: ${formatDate(todo.toCompleteByDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Divider(Modifier.padding(vertical = 8.dp))

            Text(
                "Tags: ${tagsAsState.joinToString { it.title }}",
                style = MaterialTheme.typography.bodyMedium
            )
            Divider(Modifier.padding(vertical = 8.dp))

            Text("Latitude: ${todo.latitude}", style = MaterialTheme.typography.bodyMedium)
            Text("Longitude: ${todo.longitude}", style = MaterialTheme.typography.bodyMedium)

            // Displaying the distance based on whether it's in meters or kilometers
            if (locationViewModel?.valid() == true) {
                calculatedDistance?.let {
                    Text("Distance: ${formatDistance(it)}", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Text("Distance: Location Permission is not granted", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun BannerImageDisplay(imageBitmap: Bitmap?) {
    // Displays an image for the task if available - If no image is present show placeholder icon

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(if (imageBitmap != null) Color.Transparent else Color.LightGray)
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Todo Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "No Image",
            modifier = Modifier.size(36.dp)
        )
    }
}

fun formatDate(date: Date): String {
    // Formats a Date object into a readable string format

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(date)
}

fun formatDistance(distance: Float): String {
    // Format distance to KM if above 1000 meters

    return if (distance < 1000) {
        "${distance.toInt()} meters"
    } else {
        val distanceInKm = distance / 1000
        String.format("%.2f kilometers", distanceInKm)
    }
}
