package com.kardeiro.hailfiles.data.cache

import com.kardeiro.hailfiles.data.model.AppIndex
import com.kardeiro.hailfiles.data.model.AppDetail
import com.kardeiro.hailfiles.util.Constants

class AppCache {
    private var cachedIndex: AppIndex? = null
    private var cachedIndexTimestamp: Long = 0L
    private val detailCache = mutableMapOf<String, CachedDetail>()

    fun getIndex(): AppIndex? {
        return if (isIndexValid()) cachedIndex else null
    }

    fun saveIndex(index: AppIndex) {
        cachedIndex = index
        cachedIndexTimestamp = System.currentTimeMillis()
    }

    fun isIndexValid(): Boolean {
        return cachedIndex != null &&
            (System.currentTimeMillis() - cachedIndexTimestamp) < Constants.CACHE_TTL_MS
    }

    fun getDetail(id: String): AppDetail? {
        return detailCache[id]?.detail
    }

    fun saveDetail(id: String, detail: AppDetail) {
        detailCache[id] = CachedDetail(detail, System.currentTimeMillis())
    }

    fun invalidateIndex() {
        cachedIndex = null
        cachedIndexTimestamp = 0L
    }

    fun invalidateDetail(id: String) {
        detailCache.remove(id)
    }

    fun clearAll() {
        cachedIndex = null
        cachedIndexTimestamp = 0L
        detailCache.clear()
    }

    private data class CachedDetail(
        val detail: AppDetail,
        val timestamp: Long
    )
}
