package com.example.dzandroid.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzandroid.data.mock.MockData
import com.example.dzandroid.data.models.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием данных репозиториев
 * Содержит бизнес-логику для загрузки и управления списком репозиториев
 */
class RepoViewModel : ViewModel() {

    private val _repositories = MutableStateFlow<List<Repository>>(emptyList())
    val repositories: StateFlow<List<Repository>> = _repositories.asStateFlow()

    private val _selectedRepository = MutableStateFlow<Repository?>(null)
    val selectedRepository: StateFlow<Repository?> = _selectedRepository.asStateFlow()

    init {
        loadRepositories()
    }

    private fun loadRepositories() {
        viewModelScope.launch {
            _repositories.value = MockData.repositories
        }
    }

    fun selectRepository(repository: Repository) {
        viewModelScope.launch {
            _selectedRepository.value = repository
        }
    }

    fun clearSelection() {
        viewModelScope.launch {
            _selectedRepository.value = null
        }
    }
}