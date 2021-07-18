package ru.apmgor.todo.ui.roster

import androidx.lifecycle.ViewModel
import ru.apmgor.todo.repo.ToDoModel
import ru.apmgor.todo.repo.ToDoRepository

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {

    fun getItems() = repo.items

    fun save(model: ToDoModel) {
        repo.save(model)
    }
}