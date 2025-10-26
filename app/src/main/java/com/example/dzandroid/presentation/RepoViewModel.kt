package com.example.dzandroid.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzandroid.data.local.dao.FavoriteDao
import com.example.dzandroid.data.local.datastore.SharedPrefsManager
import com.example.dzandroid.data.local.entity.FavoriteRepository
import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.remote.result.ApiResult
import com.example.dzandroid.domain.GetReadmeUseCase
import com.example.dzandroid.domain.GetRepositoryUseCase
import com.example.dzandroid.domain.SearchRepositoriesUseCase
import com.example.dzandroid.di.FilterBadgeCache
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RepoViewModel(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getReadmeUseCase: GetReadmeUseCase,
    private val sharedPrefsManager: SharedPrefsManager,
    private val favoriteDao: FavoriteDao,
    private val filterBadgeCache: FilterBadgeCache
) : ViewModel() {

    val language: String get() = sharedPrefsManager.language
    val minRating: Int get() = sharedPrefsManager.minRating
    val recipeName: String get() = sharedPrefsManager.recipeName

    val languageFlow = MutableStateFlow(sharedPrefsManager.language)
    val minRatingFlow = MutableStateFlow(sharedPrefsManager.minRating)
    val recipeNameFlow = MutableStateFlow(sharedPrefsManager.recipeName)

    private val _repositories = MutableStateFlow<List<Repository>>(emptyList())
    val repositories: StateFlow<List<Repository>> = _repositories.asStateFlow()

    private val _favorites = MutableStateFlow<List<FavoriteRepository>>(emptyList())
    val favorites: StateFlow<List<FavoriteRepository>> = _favorites.asStateFlow()

    private val _selectedRepository = MutableStateFlow<Repository?>(null)
    val selectedRepository: StateFlow<Repository?> = _selectedRepository.asStateFlow()

    private val _loadingState = MutableStateFlow<ApiResult<Unit>>(ApiResult.Loading)
    val loadingState: StateFlow<ApiResult<Unit>> = _loadingState.asStateFlow()

    private val _repoDetailsState = MutableStateFlow<ApiResult<Unit>>(ApiResult.Success(Unit))
    val repoDetailsState: StateFlow<ApiResult<Unit>> = _repoDetailsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("android")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            loadRepositories()
            loadFavorites()
        }
    }

    fun loadRepositories(query: String = "android") {
        viewModelScope.launch {
            _loadingState.value = ApiResult.Loading
            _searchQuery.value = query

            val language = sharedPrefsManager.language
            val minRating = sharedPrefsManager.minRating
            val recipe = sharedPrefsManager.recipeName

            filterBadgeCache.showBadge = language.isNotEmpty() || minRating > 0 || recipe.isNotEmpty()

            val filteredQuery = buildString {
                append(query)
                if (language.isNotEmpty()) append(" language:${language.toLowerCase()}")
                if (recipe.isNotEmpty()) append(" $recipe in:name")
                if (minRating > 0) append(" stars:>=$minRating")
            }

            when (val result = searchRepositoriesUseCase(query = filteredQuery.trim())) {
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

    fun saveFilters(language: String, minRating: Int, recipeName: String) {
        sharedPrefsManager.saveFilters(language, minRating, recipeName)
        languageFlow.value = language
        minRatingFlow.value = minRating
        recipeNameFlow.value = recipeName
        loadRepositories(_searchQuery.value)
    }

    fun saveFiltersImmediately(language: String, minRating: Int, recipeName: String) {
        val success = sharedPrefsManager.saveFiltersSync(language, minRating, recipeName)
        if (success) {
            languageFlow.value = language
            minRatingFlow.value = minRating
            recipeNameFlow.value = recipeName
            loadRepositories(_searchQuery.value)
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteDao.getAll().collect { favoritesList ->
                _favorites.value = favoritesList
            }
        }
    }

    fun addToFavorites(repo: Repository) {
        viewModelScope.launch {
            val favorite = FavoriteRepository(
                id = repo.id,
                name = repo.name,
                owner = repo.owner,
                description = repo.description,
                stars = repo.stars,
                forks = repo.forks,
                language = repo.language,
                updatedAt = repo.updatedAt
            )
            favoriteDao.insert(favorite)
        }
    }

    fun removeFromFavorites(favorite: FavoriteRepository) {
        viewModelScope.launch {
            favoriteDao.delete(favorite)
        }
    }

    suspend fun isFavorite(id: Long): Boolean {
        return favoriteDao.isFavorite(id)
    }

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

    fun clearSelection() {
        viewModelScope.launch {
            _selectedRepository.value = null
            _repoDetailsState.value = ApiResult.Success(Unit)
        }
    }

    fun retryLoading() {
        loadRepositories(_searchQuery.value)
    }

    fun retryLoadingDetails() {
        val currentRepo = _selectedRepository.value
        if (currentRepo != null) {
            selectRepository(currentRepo)
        }
    }
}