package ru.apmgor.todo

class ToDoRepository {
    var items = listOf(
        ToDoModel(
            description = "Buy a copy of _Exploring Android_",
            isCompleted = true,
            notes = "See https://wares.commonsware.com"
        ),
        ToDoModel(
            description = "Complete all of the tutorials"
        ),
        ToDoModel(
            description = "Write an app for somebody in my community",
            notes = "Talk to some people at non-profit organizations to see what they need!"
        )
    )

    fun save(model: ToDoModel) {
        items = if (items.any { model.id == it.id }) {
            items.map { if (model.id == it.id) model else it }
        } else {
            items + model
        }
    }
}