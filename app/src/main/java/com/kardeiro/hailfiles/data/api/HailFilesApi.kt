package com.kardeiro.hailfiles.data.api

import com.kardeiro.hailfiles.data.model.AppDetail
import com.kardeiro.hailfiles.data.model.AppIndex
import retrofit2.http.GET
import retrofit2.http.Path

interface HailFilesApi {
    @GET("index.json")
    suspend fun getAppIndex(): AppIndex

    @GET("apps/{id}.json")
    suspend fun getAppDetail(@Path("id") id: String): AppDetail
}
