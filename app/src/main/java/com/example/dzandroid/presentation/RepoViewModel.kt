    package com.example.dzandroid.presentation
    
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.dzandroid.data.models.Repository
    import com.example.dzandroid.data.remote.result.ApiResult
    import com.example.dzandroid.domain.GetRepositoryUseCase
    import com.example.dzandroid.domain.GetReadmeUseCase
    import com.example.dzandroid.domain.SearchRepositoriesUseCase
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch

    /**
     * ViewModel для управления состоянием экранов с репозиториями
     * Обрабатывает бизнес-логику, управляет данными и состояниями UI
     *
     * @property searchRepositoriesUseCase use case для поиска репозиториев
     * @property getRepositoryUseCase use case для получения деталей репозитория
     * @property getReadmeUseCase use case для загрузки README
     */
    class RepoViewModel(
        private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
        private val getRepositoryUseCase: GetRepositoryUseCase,
        private val getReadmeUseCase: GetReadmeUseCase
    ) : ViewModel() {
    
        private val _repositories = MutableStateFlow<List<Repository>>(emptyList())
        val repositories: StateFlow<List<Repository>> = _repositories.asStateFlow()
    
        private val _selectedRepository = MutableStateFlow<Repository?>(null)
        val selectedRepository: StateFlow<Repository?> = _selectedRepository.asStateFlow()
    
        private val _loadingState = MutableStateFlow<ApiResult<Unit>>(ApiResult.Loading)
        val loadingState: StateFlow<ApiResult<Unit>> = _loadingState.asStateFlow()
    
        private val _repoDetailsState = MutableStateFlow<ApiResult<Unit>>(ApiResult.Success(Unit))
        val repoDetailsState: StateFlow<ApiResult<Unit>> = _repoDetailsState.asStateFlow()
    
        private val _searchQuery = MutableStateFlow("android")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
        init {
            loadRepositories()
        }

        /**
         * Загружает список репозиториев по заданному запросу
         */
        fun loadRepositories(query: String = "android") {
            viewModelScope.launch {
                _loadingState.value = ApiResult.Loading
                _searchQuery.value = query
    
                when (val result = searchRepositoriesUseCase(query = query)) {
                    is ApiResult.Success -> {
                        _repositories.value = result.data
                        _loadingState.value = ApiResult.Success(Unit)
                    }
                    is ApiResult.Error -> {
                        _loadingState.value = ApiResult.Error(result.message)
                    }
                    ApiResult.Loading -> {
                        _loadingState.value = ApiResult.Loading
                    }
                }
            }
        }

        /**
         * Выбирает репозиторий и загружает его детальную информацию
         */
        fun selectRepository(repository: Repository) {
            viewModelScope.launch {
                _selectedRepository.value = repository
                _repoDetailsState.value = ApiResult.Loading
    
                when (val result = getRepositoryUseCase(repository.owner, repository.name)) {
                    is ApiResult.Success -> {
                        val repoWithDetails = result.data
                        when (val readmeResult = getReadmeUseCase(repository.owner, repository.name)) {
                            is ApiResult.Success -> {
                                _selectedRepository.value = repoWithDetails.copy(readme = readmeResult.data)
                                _repoDetailsState.value = ApiResult.Success(Unit)
                            }
                            is ApiResult.Error -> {
                                _selectedRepository.value = repoWithDetails.copy(readme = "Не удалось загрузить README: ${readmeResult.message}")
                                _repoDetailsState.value = ApiResult.Success(Unit)
                            }
                            ApiResult.Loading -> {}
                        }
                    }
                    is ApiResult.Error -> {
                        _repoDetailsState.value = ApiResult.Error(result.message)
                    }
                    ApiResult.Loading -> {
                        _repoDetailsState.value = ApiResult.Loading
                    }
                }
            }
        }

        /** Очищает выбранный репозиторий и сбрасывает состояние деталей */
        fun clearSelection() {
            viewModelScope.launch {
                _selectedRepository.value = null
                _repoDetailsState.value = ApiResult.Success(Unit)
            }
        }

        /** Повторяет загрузку основного списка репозиториев */
        fun retryLoading() {
            loadRepositories(_searchQuery.value)
        }

        /** Повторяет загрузку детальной информации выбранного репозитория */
        fun retryLoadingDetails() {
            val currentRepo = _selectedRepository.value
            if (currentRepo != null) {
                selectRepository(currentRepo)
            }
        }
    }