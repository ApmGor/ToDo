package ru.apmgor.todo.repo

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.lang.Exception

class ImportWorker(context: Context,
                   params: WorkerParameters,
                   private val repo: ToDoRepository,
                   private val prefs: PrefsRepository)
    : CoroutineWorker(context, params) {

    override suspend fun doWork() = try {
        repo.importItems(prefs.loadWebServiceUrl())
        Result.success()
    } catch (ex: Exception) {
        Log.e("ToDo", "Exception importing items in doWork()", ex)
        Result.failure()
    }
}