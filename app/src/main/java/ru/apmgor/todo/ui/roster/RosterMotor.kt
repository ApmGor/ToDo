package ru.apmgor.todo.ui.roster

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.apmgor.todo.repo.FilterMode
import ru.apmgor.todo.repo.PrefsRepository
import ru.apmgor.todo.repo.ToDoModel
import ru.apmgor.todo.repo.ToDoRepository
import ru.apmgor.todo.report.RosterReport
import ru.apmgor.todo.ui.ErrorScenario

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val filterMode: FilterMode = FilterMode.ALL
)

sealed class Nav {
    data class ViewReport(val doc: Uri) : Nav()
    data class ShareReport(val doc: Uri) : Nav()
}

class RosterMotor(
    private val repo: ToDoRepository,
    private val report: RosterReport,
    private val prefs: PrefsRepository
    ) : ViewModel() {
    private val _states = MediatorLiveData<RosterViewState>()
    val states: LiveData<RosterViewState> = _states
    private var lastSource: LiveData<RosterViewState>? = null
    private val _navEvents =
        MutableSharedFlow<Nav>()
    val navEvents = _navEvents.asSharedFlow()
    private val _errorEvents =
        MutableSharedFlow<ErrorScenario>()
    val errorEvents = _errorEvents.asSharedFlow()

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

    fun saveReport(doc: Uri) {
        viewModelScope.launch {
            _states.value?.let { report.generate(it.items, doc) }
            _navEvents.emit(Nav.ViewReport(doc))
        }
    }

    fun shareReport() {
        viewModelScope.launch {
            val doc = report.getReportUri()
            _states.value?.let { report.generate(it.items, doc) }
            _navEvents.emit(Nav.ShareReport(doc))
        }
    }

    fun importItems() {
        viewModelScope.launch {
            try {
                repo.importItems(prefs.loadWebServiceUrl())
            } catch (ex: Exception) {
                Log.e("ToDo", "Exception importing items", ex)
                _errorEvents.emit(ErrorScenario.Import)
            }
        }
    }
}