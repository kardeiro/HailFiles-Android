package com.kardeiro.hailfiles.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kardeiro.hailfiles.ui.components.*
import com.kardeiro.hailfiles.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAppClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem to totalItems
        }.collect { (lastVisible, totalItems) ->
            if (lastVisible >= totalItems - 3 && uiState.hasMore && !uiState.isLoading) {
                viewModel.loadMore()
            }
        }
    }

    Scaffold(
        topBar = {
            HailFilesTopAppBar(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.loadApps(forceRefresh = true) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = Spacing.lg,
                end = Spacing.lg,
                top = Spacing.sm,
                bottom = Spacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item(key = "hero") {
                HeroSection(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) }
                )
            }

            item(key = "stats") {
                StatsBar(
                    totalCount = uiState.totalCount,
                    lastUpdate = uiState.lastUpdate
                )
            }

            item(key = "filters") {
                FilterChipsRow(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.onCategorySelected(it) }
                )
            }

            when {
                uiState.isLoading -> {
                    items(6) { index ->
                        SkeletonCard(index = index)
                    }
                }
                uiState.error != null -> {
                    item(key = "error") {
                        ErrorState(
                            message = uiState.error!!,
                            onRetry = { viewModel.loadApps() }
                        )
                    }
                }
                uiState.filteredApps.isEmpty() -> {
                    item(key = "empty") {
                        EmptyState(
                            isSearching = uiState.searchQuery.isNotBlank()
                        )
                    }
                }
                else -> {
                    itemsIndexed(
                        items = uiState.renderedApps,
                        key = { _, app -> app.id }
                    ) { index, app ->
                        AppCard(
                            app = app,
                            index = index,
                            onClick = { onAppClick(app.id) }
                        )
                    }

                    if (uiState.hasMore) {
                        item(key = "loading_more") {
                            LoadingMoreIndicator()
                        }
                    }
                }
            }
        }

        uiState.toastMessage?.let { message ->
            HailFilesToast(
                message = message,
                onDismiss = { viewModel.clearToast() }
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Cloud,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Button(onClick = onRetry) {
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun EmptyState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Filled.Search
            else Icons.Filled.Folder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = if (isSearching) "Nenhum app encontrado"
            else "Nenhum app disponível",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingMoreIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
