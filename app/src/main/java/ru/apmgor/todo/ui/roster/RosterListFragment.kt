package ru.apmgor.todo.ui.roster

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.apmgor.todo.R
import ru.apmgor.todo.databinding.TodoRosterBinding
import ru.apmgor.todo.repo.FilterMode
import ru.apmgor.todo.repo.ToDoModel

class RosterListFragment : Fragment() {
    private val motor: RosterMotor by viewModel()
    private lateinit var binding: TodoRosterBinding
    private val menuMap = mutableMapOf<FilterMode, MenuItem>()
    private val createDoc =
        registerForActivityResult(CreateDocumentHtml()) {
            motor.saveReport(it)
        }

    companion object {
        private const val TAG = "ToDo"
    }

    class CreateDocumentHtml : ActivityResultContracts.CreateDocument() {
        override fun createIntent(context: Context, input: String): Intent {
            super.createIntent(context, input)
            return Intent().apply {
                action = Intent.ACTION_CREATE_DOCUMENT
                type = "text/html"
                putExtra(Intent.EXTRA_TITLE, input)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TodoRosterBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RosterAdapter(
            layoutInflater,
            onCheckboxToggle = { motor.save(it.copy(isCompleted = !it.isCompleted)) },
            onRowClick = ::display)

        binding.items.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
        }

        motor.states.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.items)
            binding.loading.visibility = View.GONE

            when {
                state.items.isEmpty() && state.filterMode == FilterMode.ALL -> {
                    binding.empty.visibility = View.VISIBLE
                    binding.empty.setText(R.string.msg_empty)
                }
                state.items.isEmpty() -> {
                    binding.empty.visibility = View.VISIBLE
                    binding.empty.setText(R.string.msg_empty_filtered)
                }
                else -> binding.empty.visibility = View.GONE
            }
            menuMap[state.filterMode]?.isChecked = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            motor.navEvents.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED)
                .collect { nav ->
                    when (nav) {
                        is Nav.ViewReport -> viewReport(nav.doc)
                    }
                }
        }
    }

    private fun display(model: ToDoModel) {
        findNavController()
            .navigate(RosterListFragmentDirections.displayModel(model.id))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster, menu)

        menuMap.apply {
            put(FilterMode.ALL, menu.findItem(R.id.all))
            put(FilterMode.COMPLETED, menu.findItem(R.id.completed))
            put(FilterMode.OUTSTANDING, menu.findItem(R.id.outstanding))
        }

        motor.states.value?.let { menuMap[it.filterMode]?.isChecked = true }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.add -> {
                add()
                true
            }
            R.id.all -> {
                item.isChecked = true
                motor.load(FilterMode.ALL)
                true
            }
            R.id.completed -> {
                item.isChecked = true
                motor.load(FilterMode.COMPLETED)
                true
            }
            R.id.outstanding -> {
                item.isChecked = true
                motor.load(FilterMode.OUTSTANDING)
                true
            }
            R.id.save -> {
                saveReport()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun add() {
        findNavController()
            .navigate(RosterListFragmentDirections.createModel(null))
    }

    private fun saveReport() {
        createDoc.launch("report")
    }

    private fun safeStartActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (t: Throwable) {
            Log.e(TAG, "Exception starting $intent", t)
            Toast.makeText(requireActivity(), R.string.oops, Toast.LENGTH_LONG).show()
        }
    }

    private fun viewReport(uri: Uri) {
        safeStartActivity(
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
        )
    }
}