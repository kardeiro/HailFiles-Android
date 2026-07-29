package com.kardeiro.hailfiles.data.model

data class AppIndex(
    val version: Int = 0,
    val updated: String = "",
    val apps: List<AppIndexItem> = emptyList()
)

data class AppIndexItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val icon: String = "",
    val version: String = "",
    val size: String = "",
    val updated: String = ""
)
