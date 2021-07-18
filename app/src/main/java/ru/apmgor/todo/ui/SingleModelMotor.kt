package ru.apmgor.todo.ui

import androidx.lifecycle.ViewModel
import ru.apmgor.todo.repo.ToDoModel
import ru.apmgor.todo.repo.ToDoRepository

class SingleModelMotor(
    private val repo: ToDoRepository,
    private val modelId: String?
) : ViewModel() {

    fun getModel() = repo.find(modelId)

    fun save(model: ToDoModel) {
        repo.save(model)
    }

    fun delete(model: ToDoModel) {
        repo.delete(model)
    }
}