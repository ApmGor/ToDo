package ru.apmgor.todo

import android.app.Application
import android.text.format.DateUtils
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.apmgor.todo.repo.ToDoDatabase
import ru.apmgor.todo.repo.ToDoRemoteDataSource
import ru.apmgor.todo.repo.ToDoRepository
import ru.apmgor.todo.report.RosterReport
import ru.apmgor.todo.ui.SingleModelMotor
import ru.apmgor.todo.ui.roster.RosterMotor
import java.time.Instant

class ToDoApp : Application() {
    private val koinModule = module {
        single { ToDoRepository(
            get<ToDoDatabase>().todoStore(),
            get(named("appScope")),
            get()
        ) }
        single { ToDoDatabase.newInstance(androidContext()) }
        single(named("appScope")) { CoroutineScope(SupervisorJob()) }
        single {
            Handlebars().apply {
                registerHelper("dateFormat", Helper<Instant> { value, _ ->
                    DateUtils.getRelativeDateTimeString(
                        androidContext(),
                        value.toEpochMilli(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS, 0
                    )
                })
            }
        }
        single { RosterReport(androidContext(), get(), get(named("appScope"))) }
        single { OkHttpClient.Builder().build() }
        single { ToDoRemoteDataSource(get()) }
        viewModel { RosterMotor(get(), get()) }
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