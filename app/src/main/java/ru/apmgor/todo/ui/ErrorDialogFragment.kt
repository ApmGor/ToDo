package ru.apmgor.todo.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.apmgor.todo.R

class ErrorDialogFragment : DialogFragment() {
    private val args: ErrorDialogFragmentArgs by navArgs()

    companion object {
        const val KEY_RETRY = "retryRequested"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(args.title)
            .setMessage(args.message)
            .setPositiveButton(R.string.retry) { _, _ -> onRetryRequest() }
            .setNegativeButton(R.string.cancel) { _, _ -> clearImportError() }
            .create()
    }

    private fun onRetryRequest() {
        findNavController()
            .previousBackStackEntry?.savedStateHandle?.set(KEY_RETRY, args.scenario)
    }

    private fun clearImportError() {
        findNavController()
            .previousBackStackEntry
            ?.savedStateHandle
            ?.remove<ErrorScenario>(KEY_RETRY)
    }
}

enum class ErrorScenario { Import }