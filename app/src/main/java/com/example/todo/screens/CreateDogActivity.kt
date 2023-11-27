package com.example.tailtasks.screens

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import java.io.ByteArrayOutputStream
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.data.TodoDatabase
import com.example.todo.entities.DogEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDogScreen(onDogAdded: (DogEntity) -> Unit) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf(Date()) }
    var notes by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DogImagePicker(imageBitmap) { newBitmap -> imageBitmap = newBitmap }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Breed") },
            modifier = Modifier.fillMaxWidth()
        )

        val context = LocalContext.current
        BirthdayPicker(
            context = context,
            selectedDate = birthday,
            onDateSelected = { newDate ->
                birthday = newDate
            }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buttonScope = rememberCoroutineScope()
        //val dao = TodoDatabase.(LocalContext.current).dogDao()
        fun insertOnClick(dog: DogEntity){
            buttonScope.launch {
                //dao.insert(dog)
            }
        }
        Button(
            onClick = {
                // Convert the Bitmap to ByteArray
                val imageBytes = imageBitmap?.let { bitmap ->
                    ByteArrayOutputStream().apply {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
                    }.toByteArray()
                }

                // Create a Dog object to be saved in the database
                val dog = DogEntity(
                    // Assuming id is autogenerated
                    name = name,
                    breed = breed,
                    birthdayDate = birthday,
                    notes = notes,
                    imageBytes = imageBytes,
                    age = 0,
                    currentMood = Float.NaN,
                    deleted = false,
                    id = 0
                )
                insertOnClick(dog)
                onDogAdded(dog)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Create Dog")
        }
    }
}


@Composable
fun DogImagePicker(imageBitmap: Bitmap?, onImageCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { onImageCaptured(it) }
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
            .background(Color.LightGray)
            .clickable { launcher.launch(null) }
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } ?: Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Take Picture",
            modifier = Modifier
                .size(24.dp)
                .background(Color.White, CircleShape)
                .padding(4.dp)
        )
    }
}

@Composable
fun BirthdayPicker(
    context: Context,
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateString = selectedDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "Select Date"

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = dateString)
    }

    if (showDialog) {
        val currentCalendar = Calendar.getInstance()
        selectedDate?.let { currentCalendar.time = it }

        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDayOfMonth)
                }
                onDateSelected(selectedCalendar.time)
                showDialog = false
            },
            year,
            month,
            day
        ).show()
    }
}