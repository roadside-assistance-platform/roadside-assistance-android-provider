package esi.roadside.assistance.provider.main.presentation

import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi

sealed interface Action {
        data class Navigate(val route: NavRoutes): Action
        data class SendEvent(val event: Event): Action
        data class SetLocation(val location: Point?): Action
        data object EnableProfileEditing: Action
        data object CancelProfileEditing: Action
        data class EditClient(val client: ProviderUi): Action
        data object ConfirmProfileEditing: Action
        data object Logout: Action
        data class SelectService(val index: Int): Action
        data object UnSelectService: Action
        data class AcceptService(val index: Int): Action
        data object Arrived: Action
        data object HideFinishDialog: Action
}