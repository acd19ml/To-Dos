package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO

class TodosViewModelFactory(private val todoDAO: TodoDAO, private val dogDAO: DogDAO
                            ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodosViewModel(todoDAO, dogDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

