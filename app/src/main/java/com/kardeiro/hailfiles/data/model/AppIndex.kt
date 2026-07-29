package com.kardeiro.hailfiles.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppIndex(
    val version: Int = 0,
    val updated: String = "",
    val apps: List<AppIndexItem> = emptyList()
)

@Serializable
data class AppIndexItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val icon: String = "",
    val version: String = "",
    val size: String = "",
    val updated: String = ""
)
