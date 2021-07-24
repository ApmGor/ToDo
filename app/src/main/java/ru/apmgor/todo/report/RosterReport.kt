package ru.apmgor.todo.report

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.jknack.handlebars.Handlebars
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.apmgor.todo.BuildConfig
import ru.apmgor.todo.R
import ru.apmgor.todo.repo.ToDoModel
import java.io.File

class RosterReport(
    private val context: Context,
    engine: Handlebars,
    private val appScope: CoroutineScope
) {
    private val template =
        engine.compileInline(context.getString(R.string.report_template))

    companion object {
        private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
    }

    suspend fun generate(content: List<ToDoModel>, doc: Uri) {
        withContext(appScope.coroutineContext + Dispatchers.IO) {
            context.contentResolver.openOutputStream(doc, "rwt")?.writer()?.use { osw ->
                osw.write(template.apply(content))
                osw.flush()
            }
        }
    }

    suspend fun getReportUri(): Uri =
        withContext(appScope.coroutineContext + Dispatchers.IO) {
            val shared = File(context.cacheDir, "shared").also { it.mkdirs() }
            val reportFile = File(shared, "report.html")
            FileProvider.getUriForFile(context, AUTHORITY, reportFile)
        }
}