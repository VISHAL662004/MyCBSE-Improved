package com.example.mycbseguideimproved.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycbseguideimproved.data.model.Content
import com.example.mycbseguideimproved.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ContentUiState {
    data object Loading : ContentUiState()
    data class Success(val content: Content) : ContentUiState()
    data class Error(val message: String) : ContentUiState()
}

class ContentViewModel(private val repository: ContentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ContentUiState>(ContentUiState.Loading)
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()

    fun loadContent(contentId: Int) {
        viewModelScope.launch {
            _uiState.value = ContentUiState.Loading
            try {
                val content = repository.getContent(contentId)
                if (content != null) {
                    _uiState.value = ContentUiState.Success(content)
                } else {
                    _uiState.value = ContentUiState.Error("Failed to load content")
                }
            } catch (e: Exception) {
                _uiState.value = ContentUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
} 