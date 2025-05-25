package esi.roadside.assistance.provider.main.presentation

import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi

sealed interface Action {
        data class SendEvent(val event: Event): Action
        data class SetLocation(val location: Point?): Action
        data object Logout: Action
        data class SelectService(val index: Int): Action
        data object UnSelectService: Action
        data class AcceptService(val index: Int): Action
        data class SetMessage(val message: String): Action
        data object SendMessage: Action
        data object LocationUpdate: Action
        data object HideFinishDialog: Action
        data object Arrived: Action
        data object RefreshUser: Action
        data object RemoveRoutes: Action
        data object FetchServices: Action
}