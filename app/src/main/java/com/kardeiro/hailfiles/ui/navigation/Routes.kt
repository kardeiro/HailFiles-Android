package com.kardeiro.hailfiles.ui.navigation

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{appId}"

    fun detail(appId: String): String = "detail/$appId"
}
