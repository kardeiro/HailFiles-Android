package com.kardeiro.hailfiles.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardeiro.hailfiles.data.model.AppIndexItem
import com.kardeiro.hailfiles.data.repository.HailFilesRepository
import com.kardeiro.hailfiles.di.AppModule
import com.kardeiro.hailfiles.util.Constants
import com.kardeiro.hailfiles.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val apps: List<AppIndexItem> = emptyList(),
    val filteredApps: List<AppIndexItem> = emptyList(),
    val renderedApps: List<AppIndexItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val toastMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "all",
    val categories: List<CategoryInfo> = emptyList(),
    val totalCount: Int = 0,
    val lastUpdate: String = "",
    val renderedCount: Int = 0,
    val hasMore: Boolean = true
)

data class CategoryInfo(
    val id: String,
    val label: String,
    val icon: String,
    val count: Int
)

class HomeViewModel(
    private val repository: HailFilesRepository = AppModule.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                if (forceRefresh) it.copy(isRefreshing = true)
                else it.copy(isLoading = true, error = null)
            }

            if (forceRefresh) {
                repository.invalidateIndex()
            }

            when (val result = repository.getAppIndex(forceRefresh)) {
                is NetworkResult.Success -> {
                    val index = result.data
                    val categories = buildCategories(index.apps)
                    val filtered = applyFilters(index.apps, _uiState.value.searchQuery, _uiState.value.selectedCategory)
                    val initialCount = minOf(filtered.size, Constants.PAGE_SIZE)

                    _uiState.update {
                        it.copy(
                            apps = index.apps,
                            filteredApps = filtered,
                            renderedApps = filtered.take(initialCount),
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            totalCount = index.apps.size,
                            lastUpdate = index.updated,
                            categories = categories,
                            renderedCount = initialCount,
                            hasMore = filtered.size > initialCount,
                            toastMessage = if (forceRefresh) "Lista atualizada!" else null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message,
                            toastMessage = if (forceRefresh) "Erro ao atualizar" else null
                        )
                    }
                }
                is NetworkResult.Loading -> { /* handled by initial state */ }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reapplyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        reapplyFilters()
    }

    fun loadMore() {
        val state = _uiState.value
        val currentCount = state.renderedCount
        val newCount = minOf(currentCount + Constants.PAGE_SIZE, state.filteredApps.size)

        if (newCount > currentCount) {
            _uiState.update {
                it.copy(
                    renderedCount = newCount,
                    renderedApps = state.filteredApps.take(newCount),
                    hasMore = newCount < state.filteredApps.size
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun reapplyFilters() {
        val state = _uiState.value
        val filtered = applyFilters(state.apps, state.searchQuery, state.selectedCategory)
        val initialCount = minOf(filtered.size, Constants.PAGE_SIZE)

        _uiState.update {
            it.copy(
                filteredApps = filtered,
                renderedApps = filtered.take(initialCount),
                renderedCount = initialCount,
                hasMore = filtered.size > initialCount
            )
        }
    }

    private fun applyFilters(apps: List<AppIndexItem>, query: String, category: String): List<AppIndexItem> {
        return apps.filter { app ->
            val matchesCategory = category == "all" || app.category == category
            val matchesSearch = query.isBlank() ||
                app.name.contains(query, ignoreCase = true) ||
                app.category.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    private fun buildCategories(apps: List<AppIndexItem>): List<CategoryInfo> {
        val counts = apps.groupBy { it.category }.mapValues { it.value.size }
        return listOf(
            CategoryInfo("all", "Todos", "folder", apps.size),
            CategoryInfo("apps", "Apps", "apps", counts["apps"] ?: 0),
            CategoryInfo("music", "Música", "music_note", counts["music"] ?: 0),
            CategoryInfo("gallery", "Galeria", "photo_library", counts["gallery"] ?: 0)
        )
    }
}
