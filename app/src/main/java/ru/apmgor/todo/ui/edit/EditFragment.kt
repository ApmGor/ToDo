package ru.apmgor.todo.ui.edit

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.apmgor.todo.R
import ru.apmgor.todo.databinding.TodoEditBinding
import ru.apmgor.todo.repo.ToDoModel
import ru.apmgor.todo.ui.SingleModelMotor

class EditFragment : Fragment() {
    private lateinit var binding: TodoEditBinding
    private val args: EditFragmentArgs by navArgs()
    private val motor: SingleModelMotor by viewModel { parametersOf(args.modelId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TodoEditBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        motor.states.observe(viewLifecycleOwner) { state ->
            if (savedInstanceState == null) {
                state.item?.let {
                    binding.apply {
                        isCompleted.isChecked = it.isCompleted
                        desc.setText(it.description)
                        notes.setText(it.notes)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_edit, menu)
        menu.findItem(R.id.delete).isVisible = args.modelId != null

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.save -> {
                save()
                true
            }
            R.id.delete -> {
                delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun save() {
        val model = motor.states.value?.item
        val edited = model?.copy(
            description = binding.desc.text.toString(),
            isCompleted = binding.isCompleted.isChecked,
            notes = binding.notes.text.toString()
        ) ?: ToDoModel(
            description = binding.desc.text.toString(),
            isCompleted = binding.isCompleted.isChecked,
            notes = binding.notes.text.toString()
        )
        edited.let { motor.save(it) }
        navToDisplay()
    }

    private fun delete() {
        val model = motor.states.value?.item

        model?.let { motor.delete(it) }
        navToList()
    }

    private fun navToDisplay() {
        hideKeyboard()
        findNavController().popBackStack()
    }

    private fun navToList() {
        hideKeyboard()
        findNavController().popBackStack(R.id.rosterListFragment, false)
    }

    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}