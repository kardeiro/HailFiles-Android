package com.kardeiro.hailfiles.di

import com.kardeiro.hailfiles.data.cache.AppCache
import com.kardeiro.hailfiles.data.repository.HailFilesRepository

object AppModule {
    private val cache by lazy { AppCache() }
    val repository by lazy { HailFilesRepository(cache) }
}
