package esi.roadside.assistance.provider.main.data.dto

data class LocationResponse(
    val bbox: List<Int>,
    val limit: Int,
    val q: String,
    val types: List<String>
)