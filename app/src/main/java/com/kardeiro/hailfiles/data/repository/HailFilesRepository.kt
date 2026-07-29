package com.kardeiro.hailfiles.data.repository

import com.kardeiro.hailfiles.data.api.ApiClient
import com.kardeiro.hailfiles.data.cache.AppCache
import com.kardeiro.hailfiles.data.model.AppDetail
import com.kardeiro.hailfiles.data.model.AppIndex
import com.kardeiro.hailfiles.util.NetworkResult

class HailFilesRepository(
    private val cache: AppCache = AppCache()
) {
    private val api = ApiClient.api

    suspend fun getAppIndex(forceRefresh: Boolean = false): NetworkResult<AppIndex> {
        if (!forceRefresh) {
            cache.getIndex()?.let { return NetworkResult.Success(it) }
        }

        return try {
            val index = api.getAppIndex()
            cache.saveIndex(index)
            NetworkResult.Success(index)
        } catch (e: Exception) {
            cache.getIndex()?.let { return NetworkResult.Success(it) }
            NetworkResult.Error("Falha ao carregar lista de apps", e)
        }
    }

    suspend fun getAppDetail(id: String, forceRefresh: Boolean = false): NetworkResult<AppDetail> {
        if (!forceRefresh) {
            cache.getDetail(id)?.let { return NetworkResult.Success(it) }
        }

        return try {
            val detail = api.getAppDetail(id)
            cache.saveDetail(id, detail)
            NetworkResult.Success(detail)
        } catch (e: Exception) {
            cache.getDetail(id)?.let { return NetworkResult.Success(it) }
            NetworkResult.Error("Falha ao carregar detalhes do app", e)
        }
    }

    fun invalidateIndex() {
        cache.invalidateIndex()
    }
}
