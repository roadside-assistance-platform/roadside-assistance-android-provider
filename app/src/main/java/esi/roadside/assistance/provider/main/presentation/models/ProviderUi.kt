package esi.roadside.assistance.provider.main.presentation.models

import android.net.Uri
import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.ProviderInfo

data class ProviderUi(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val photo: Uri? = null,
    val isApproved: Boolean = false,
    val averageRating: Double? = null,
    val location: LocationModel = LocationModel(0.0, 0.0),
    val categories: Set<Categories> = emptySet(),
) {
    fun toUpdateModel() = UpdateModel(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        categories = this@ProviderUi.categories,
        location = location,
    )
}
