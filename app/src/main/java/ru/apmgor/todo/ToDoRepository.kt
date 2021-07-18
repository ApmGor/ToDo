package ru.apmgor.todo

class ToDoRepository {
    var items = emptyList<ToDoModel>()

    fun save(model: ToDoModel) {
        items = if (items.any { model.id == it.id }) {
            items.map { if (model.id == it.id) model else it }
        } else {
            items + model
        }
    }

    fun find(modelId: String?) = items.find { modelId == it.id }

    fun delete(model: ToDoModel) {
        items = items.filter { it.id != model.id }
    }
}