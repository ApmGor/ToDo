package ru.apmgor.todo

import android.app.Application
import android.text.format.DateUtils
import androidx.work.*
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.apmgor.todo.repo.*
import ru.apmgor.todo.report.RosterReport
import ru.apmgor.todo.ui.SingleModelMotor
import ru.apmgor.todo.ui.roster.RosterMotor
import java.time.Instant
import java.util.concurrent.TimeUnit

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
        single { PrefsRepository(androidContext()) }
        viewModel { RosterMotor(get(), get(), get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
        worker { ImportWorker(androidContext(), get(), get(), get()) }
    }

    companion object {
        private const val TAG_IMPORT_WORK = "doPeriodicImport"
    }

    @KoinExperimentalAPI
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ToDoApp)
            workManagerFactory()
            modules(koinModule)
        }

        scheduleWork()
    }

    private fun scheduleWork() {
        val prefs: PrefsRepository by inject()
        val appScope: CoroutineScope by inject(named("appScope"))
        val workManager = WorkManager.getInstance(this)

        appScope.launch {
            prefs.observeImportChanges().collect {
                if (it) {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request =
                        PeriodicWorkRequestBuilder<ImportWorker>(15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .addTag(TAG_IMPORT_WORK)
                            .build()

                    workManager.enqueueUniquePeriodicWork(
                        TAG_IMPORT_WORK,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                    )
                } else {
                    workManager.cancelAllWorkByTag(TAG_IMPORT_WORK)
                }
            }
        }
    }
}