package com.kardeiro.hailfiles.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppDetail(
    val id: String = "",
    val name: String = "",
    val version: String = "",
    val description: String = "",
    val longDescription: String = "",
    val icon: String = "",
    val file: String = "",
    val mirrors: List<AppMirror> = emptyList(),
    val size: String = "",
    val category: String = "",
    val updated: String = "",
    val author: String = "",
    val downloads: Int = 0,
    val minAndroid: String = "",
    val requiresShizuku: Boolean = false,
    val language: String = "",
    val website: String = "",
    val sourceCode: String = "",
    val permissions: List<String> = emptyList(),
    val changelog: List<ChangelogEntry> = emptyList(),
    val screenshots: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)

@Serializable
data class AppMirror(
    val id: String = "",
    val label: String = "",
    val url: String = "",
    val type: String = ""
)

@Serializable
data class ChangelogEntry(
    val version: String = "",
    val date: String = "",
    val changes: List<String> = emptyList()
)
