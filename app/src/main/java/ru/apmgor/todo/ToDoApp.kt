package ru.apmgor.todo

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.get
import org.koin.dsl.module
import ru.apmgor.todo.repo.ToDoDatabase
import ru.apmgor.todo.repo.ToDoRepository
import ru.apmgor.todo.ui.SingleModelMotor
import ru.apmgor.todo.ui.roster.RosterMotor

class ToDoApp : Application() {
    private val koinModule = module {
        single { ToDoRepository(
            get<ToDoDatabase>().todoStore(),
            get(named("appScope"))
        ) }
        single { ToDoDatabase.newInstance(androidContext()) }
        single(named("appScope")) { CoroutineScope(SupervisorJob()) }
        viewModel { RosterMotor(get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ToDoApp)
            modules(koinModule)
        }
    }
}