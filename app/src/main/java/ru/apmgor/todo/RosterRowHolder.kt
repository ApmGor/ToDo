package ru.apmgor.todo

import androidx.recyclerview.widget.RecyclerView
import ru.apmgor.todo.databinding.TodoRowBinding

class RosterRowHolder(private val binding: TodoRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ToDoModel) {
        binding.apply {
            isCompleted.isChecked = item.isCompleted
            desc.text = item.description
        }
    }
}