package com.kardeiro.hailfiles.data.repository

import com.kardeiro.hailfiles.data.api.HailFilesApi
import com.kardeiro.hailfiles.data.cache.AppCache
import com.kardeiro.hailfiles.data.model.AppIndex
import com.kardeiro.hailfiles.data.model.AppIndexItem
import com.kardeiro.hailfiles.util.NetworkResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class HailFilesRepositoryTest {

    private lateinit var repository: HailFilesRepository
    private lateinit var cache: AppCache
    private lateinit var api: HailFilesApi

    @Before
    fun setup() {
        cache = AppCache()
        api = mock(HailFilesApi::class.java)
        repository = HailFilesRepository(cache)
    }

    @Test
    fun `test cache returns cached index when valid`() = runTest {
        val mockIndex = AppIndex(
            version = 1,
            updated = "2025-06-25",
            apps = listOf(
                AppIndexItem("test", "Test App", "apps", "icons/test.png", "1.0", "5MB", "2025-06-25")
            )
        )
        cache.saveIndex(mockIndex)

        val result = repository.getAppIndex()
        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.apps.size)
        assertEquals("Test App", result.data.apps[0].name)
    }

    @Test
    fun `test getAppIndex returns error on failure and no cache`() = runTest {
        cache.invalidateIndex()
        val result = repository.getAppIndex()
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `test invalidateIndex clears cache`() = runTest {
        val mockIndex = AppIndex(version = 1, updated = "", apps = emptyList())
        cache.saveIndex(mockIndex)
        repository.invalidateIndex()
        assertNull(cache.getIndex())
    }

    @Test
    fun `test getAppDetail returns cached detail when available`() = runTest {
        val detail = com.kardeiro.hailfiles.data.model.AppDetail(
            id = "test", name = "Test App", version = "1.0"
        )
        cache.saveDetail("test", detail)

        val result = repository.getAppDetail("test")
        assertTrue(result is NetworkResult.Success)
        assertEquals("Test App", (result as NetworkResult.Success).data.name)
    }
}
