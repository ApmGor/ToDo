package ru.apmgor.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.apmgor.todo.databinding.TodoRosterBinding

class RosterListFragment : Fragment() {
    private val motor: RosterMotor by viewModel()
    private lateinit var binding: TodoRosterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TodoRosterBinding.inflate(inflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RosterAdapter(layoutInflater) {
            motor.save(it.copy(isCompleted = !it.isCompleted))
        }

        binding.items.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )

            adapter.submitList(motor.items)
            binding.empty.visibility = View.GONE
        }
    }
}