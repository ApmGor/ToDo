package ru.apmgor.todo.ui.roster

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.apmgor.todo.R
import ru.apmgor.todo.databinding.TodoRosterBinding
import ru.apmgor.todo.repo.ToDoModel

class RosterListFragment : Fragment() {
    private val motor: RosterMotor by viewModel()
    private lateinit var binding: TodoRosterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TodoRosterBinding.inflate(inflater, container, false).apply { binding = this }.root

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

            adapter.submitList(motor.getItems())
        }
        binding.empty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun display(model: ToDoModel) {
        findNavController()
            .navigate(RosterListFragmentDirections.displayModel(model.id))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.add -> {
                add()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun add() {
        findNavController()
            .navigate(RosterListFragmentDirections.createModel(null))
    }
}