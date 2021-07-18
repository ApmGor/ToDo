package ru.apmgor.todo.ui.roster

import androidx.recyclerview.widget.RecyclerView
import ru.apmgor.todo.databinding.TodoRowBinding
import ru.apmgor.todo.repo.ToDoModel

class RosterRowHolder(
    private val binding: TodoRowBinding,
    val onCheckboxToggle: (ToDoModel) -> Unit,
    val onRowClick: (ToDoModel) -> Unit
    ) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ToDoModel) {
        binding.apply {
            root.setOnClickListener { onRowClick(model) }
            isCompleted.isChecked = model.isCompleted
            isCompleted.setOnCheckedChangeListener { _, _ -> onCheckboxToggle(model) }
            desc.text = model.description
        }
    }
}