package ru.apmgor.todo

import android.app.Application
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.apmgor.todo.repo.ToDoRepository
import ru.apmgor.todo.ui.SingleModelMotor
import ru.apmgor.todo.ui.roster.RosterMotor

class ToDoApp : Application() {
    private val koinModule = module {
        single { ToDoRepository() }
        viewModel { RosterMotor(get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            modules(koinModule)
        }
    }
}