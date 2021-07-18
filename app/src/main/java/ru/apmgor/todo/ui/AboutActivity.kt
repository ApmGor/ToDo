package ru.apmgor.todo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.apmgor.todo.R
import ru.apmgor.todo.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAboutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.toolbar.title = getString(R.string.app_name)
        binding.about.loadUrl("file:///android_asset/about.html")
    }
}