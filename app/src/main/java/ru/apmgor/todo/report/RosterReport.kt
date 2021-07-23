package ru.apmgor.todo.report

import android.content.Context
import android.net.Uri
import com.github.jknack.handlebars.Handlebars
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.apmgor.todo.R
import ru.apmgor.todo.repo.ToDoModel

class RosterReport(
    private val context: Context,
    engine: Handlebars,
    private val appScope: CoroutineScope
) {
    private val template =
        engine.compileInline(context.getString(R.string.report_template))

    suspend fun generate(content: List<ToDoModel>, doc: Uri) {
        withContext(appScope.coroutineContext + Dispatchers.IO) {
            context.contentResolver.openOutputStream(doc, "rwt")?.writer()?.use { osw ->
                osw.write(template.apply(content))
                osw.flush()
            }
        }
    }
}