package com.example.mycbseguideimproved

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycbseguideimproved.data.api.ApiClient
import com.example.mycbseguideimproved.data.repository.CategoryRepository
import com.example.mycbseguideimproved.data.repository.ContentRepository
import com.example.mycbseguideimproved.ui.screen.ContentScreen
import com.example.mycbseguideimproved.ui.screen.HomeScreen
import com.example.mycbseguideimproved.ui.screen.LoginScreen
import com.example.mycbseguideimproved.ui.theme.MyCBSEGuideImprovedTheme
import com.example.mycbseguideimproved.ui.viewmodel.AuthViewModel
import com.example.mycbseguideimproved.ui.viewmodel.CategoryViewModel
import com.example.mycbseguideimproved.ui.viewmodel.ContentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiClient.create()
        val categoryRepository = CategoryRepository(apiService)
        val contentRepository = ContentRepository(apiService)

        setContent {
            MyCBSEGuideImprovedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val categoryViewModel = viewModel { CategoryViewModel(categoryRepository) }
                    val contentViewModel = viewModel { ContentViewModel(contentRepository) }
                    val authViewModel: AuthViewModel = viewModel()

                    if (!authViewModel.isAuthenticated) {
                        LoginScreen(viewModel = authViewModel)
                    } else {
                        // State to track current screen
                        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                        
                        when (val screen = currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    viewModel = categoryViewModel,
                                    userName = authViewModel.userName,
                                    onCategoryClick = { categoryId, categoryName ->
                                        // Only open ContentScreen for CBSE category (checking by name)
                                        if (categoryName.contains("CBSE", ignoreCase = true)) {
                                            currentScreen = Screen.Content(contentId = 28297)
                                        }
                                    }
                                )
                            }
                            is Screen.Content -> {
                                ContentScreen(
                                    viewModel = contentViewModel,
                                    contentId = screen.contentId,
                                    onBackPressed = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Sealed class for navigation
sealed class Screen {
    data object Home : Screen()
    data class Content(val contentId: Int) : Screen()
}