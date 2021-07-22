package ru.apmgor.todo.ui.roster

import androidx.lifecycle.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.apmgor.todo.repo.FilterMode
import ru.apmgor.todo.repo.ToDoModel
import ru.apmgor.todo.repo.ToDoRepository

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val filterMode: FilterMode = FilterMode.ALL
)

class RosterMotor(private val repo: ToDoRepository) : ViewModel() {
    private val _states = MediatorLiveData<RosterViewState>()
    val states: LiveData<RosterViewState> = _states
    private var lastSource: LiveData<RosterViewState>? = null

    init {
        load(FilterMode.ALL)
    }

    fun load(filterMode: FilterMode) {
        lastSource?.let { _states.removeSource(it) }

        val items =
            repo.items(filterMode).map { RosterViewState(it, filterMode) }.asLiveData()

        _states.addSource(items) { viewstate ->
            _states.value = viewstate
        }

        lastSource = items
    }

    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repo.save(model)
        }
    }
}