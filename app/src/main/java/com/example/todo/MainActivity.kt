package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo.entities.TodoEntity
import com.example.todo.screens.ActiveTasksScreen
import com.example.todo.screens.CompletedTasksHistoryScreen
import com.example.todo.screens.TodoForm
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.viewmodels.TodosViewModel
import com.example.todo.viewmodels.TodosViewModelFactory
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.screens.AddDogScreen


class MainActivity : ComponentActivity() {
    private val todosViewModel: TodosViewModel by viewModels {
        TodosViewModelFactory(
            (application as TodoApplication).todoDAO,
            (application as TodoApplication).dogDAO,
            (application as TodoApplication).tagDAO,
            (application as TodoApplication).moodDAO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(todosViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(todosViewModel: TodosViewModel) {
    val navController = rememberNavController()
    var showTodoForm by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { navController.navigate("history") }) {
                    Icon(Icons.Filled.History, contentDescription = "Completed Tasks")
                }
            }
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = {
                    showTodoForm = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Add a button to navigate to the dog creation screen
                FloatingActionButton(onClick = {
                    navController.navigate("createDogScreen")
                }) {
                    Icon(Icons.Filled.Pets, contentDescription = "Add Dog")
                }
            }
        },
    ) { paddingValues ->
        if (showTodoForm) {
            TodoForm(todosViewModel) {
                showTodoForm = false
            }
        } else {
            NavHost(navController, startDestination = "tasks", modifier = Modifier.padding(paddingValues)) {
                composable("tasks") { ActiveTasksScreen(todosViewModel) }
                composable("history") {
                    // Force UI refresh when navigating to the "history" screen
                    LaunchedEffect(Unit) {
                        todosViewModel.loadCompletedTasks()
                    }
                    CompletedTasksHistoryScreen(todosViewModel)
                }
                composable("createDogScreen") {
                    AddDogScreen(todosViewModel, navController = navController) { addedDog ->
                        println("New dog added: ${addedDog.name}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRow(
    task: TodoEntity,
    onTaskCheckedChange: (Boolean) -> Unit,
    onTaskTextChange: (String) -> Unit,

    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onTaskCheckedChange
        )
        TextField(
            value = task.title,
            onValueChange = onTaskTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            placeholder = { Text("Enter task here")}
        )

    }
}



