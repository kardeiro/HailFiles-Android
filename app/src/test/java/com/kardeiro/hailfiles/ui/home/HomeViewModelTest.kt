package com.kardeiro.hailfiles.ui.home

import com.kardeiro.hailfiles.data.model.AppIndex
import com.kardeiro.hailfiles.data.model.AppIndexItem
import com.kardeiro.hailfiles.data.repository.HailFilesRepository
import com.kardeiro.hailfiles.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: HailFilesRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test search filters apps by name`() = runTest {
        val apps = listOf(
            AppIndexItem("1", "Camera App", "apps", "", "1.0", "5MB", "2025-01-01"),
            AppIndexItem("2", "Music Player", "music", "", "2.0", "10MB", "2025-01-02"),
            AppIndexItem("3", "Gallery Pro", "gallery", "", "3.0", "15MB", "2025-01-03")
        )
        val index = AppIndex(version = 1, updated = "2025-06-25", apps = apps)
        `when`(repository.getAppIndex(false)).thenReturn(NetworkResult.Success(index))
    }
}
