package ru.apmgor.todo.ui.prefs

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.apmgor.todo.R

class PrefsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }
}