package esi.roadside.assistance.provider.main.presentation.routes.home.request

import esi.roadside.assistance.provider.main.domain.Categories

data class RequestAssistanceState(
    val sheetVisible: Boolean = false,
    val category: Categories = Categories.TOWING,
    val description: String = ""
)
