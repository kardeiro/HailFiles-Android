package com.kardeiro.hailfiles.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardeiro.hailfiles.data.model.AppDetail
import com.kardeiro.hailfiles.data.repository.HailFilesRepository
import com.kardeiro.hailfiles.di.AppModule
import com.kardeiro.hailfiles.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val appDetail: AppDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val appId: String = ""
)

class DetailViewModel(
    private val repository: HailFilesRepository = AppModule.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadAppDetail(id: String, forceRefresh: Boolean = false) {
        if (id == _uiState.value.appId && !forceRefresh) return

        _uiState.update { DetailUiState(appId = id, isLoading = true) }

        viewModelScope.launch {
            when (val result = repository.getAppDetail(id, forceRefresh)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            appDetail = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
