package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.HRMRepository
import com.example.data.SchoolHRMDatabase
import com.example.ui.screens.HRMMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HRMViewModel
import com.example.ui.viewmodel.HRMViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Instantiate local SQLite database with automatic pre-population on first launch
    val database = SchoolHRMDatabase.getDatabase(this, lifecycleScope)
    val repository = HRMRepository(database.hrmDao())

    val viewModel: HRMViewModel by viewModels {
      HRMViewModelFactory(repository)
    }

    setContent {
      MyApplicationTheme {
        HRMMainScreen(viewModel = viewModel)
      }
    }
  }
}

