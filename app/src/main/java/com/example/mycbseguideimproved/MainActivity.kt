package com.example.mycbseguideimproved

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycbseguideimproved.data.api.ApiClient
import com.example.mycbseguideimproved.data.repository.CategoryRepository
import com.example.mycbseguideimproved.ui.screen.HomeScreen
import com.example.mycbseguideimproved.ui.theme.MyCBSEGuideImprovedTheme
import com.example.mycbseguideimproved.ui.viewmodel.CategoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiClient.create()
        val repository = CategoryRepository(apiService)

        setContent {
            MyCBSEGuideImprovedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel { CategoryViewModel(repository) }
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}