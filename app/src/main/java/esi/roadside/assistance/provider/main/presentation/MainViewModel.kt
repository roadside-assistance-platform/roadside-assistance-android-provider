package esi.roadside.assistance.provider.main.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event.ExitToAuthActivity
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import esi.roadside.assistance.provider.core.util.account.AccountManager
import esi.roadside.assistance.provider.main.domain.models.FetchServicesModel
import esi.roadside.assistance.provider.main.domain.models.toLocationModel
import esi.roadside.assistance.provider.main.domain.repository.ServiceAction.Accept
import esi.roadside.assistance.provider.main.domain.repository.ServiceAction.Arrived
import esi.roadside.assistance.provider.main.domain.repository.ServiceAction.Finish
import esi.roadside.assistance.provider.main.domain.repository.ServiceAction.LocationUpdate
import esi.roadside.assistance.provider.main.domain.repository.ServiceAction.SelectService
import esi.roadside.assistance.provider.main.domain.repository.ServiceAction.UnSelectService
import esi.roadside.assistance.provider.main.domain.repository.ServiceManager
import esi.roadside.assistance.provider.main.domain.use_cases.DirectionsUseCase
import esi.roadside.assistance.provider.main.domain.use_cases.FetchServices
import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.Refresh
import esi.roadside.assistance.provider.main.presentation.routes.home.HomeUiState
import esi.roadside.assistance.provider.main.presentation.routes.home.ProviderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    val serviceManager: ServiceManager,
    accountManager: AccountManager,
    val directionsUseCase: DirectionsUseCase,
    val refreshUseCase: Refresh,
    val fetchServices: FetchServices,
    val logoutUseCase: Logout,
): ViewModel() {
    private val _servicesHistory = MutableStateFlow<FetchServicesModel?>(null)
    val servicesHistory = _servicesHistory.asStateFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val user = accountManager.getUserFlow()

    val serviceState = serviceManager.service

    val isApproved = accountManager.getUserFlow().map { it.isApproved }

    init {
        Log.i("MainViewModel", "init")
        viewModelScope.launch(Dispatchers.Main) {
            launch(Dispatchers.IO) {
                user.map { it.id to it.serviceCategories }.collectLatest { (id, categories) ->
                    onAction(Action.FetchServices)
                    serviceManager.listen(id, categories)  {
                        _homeUiState.first().location?.toLocationModel()
                    }
                }
            }
            launch(Dispatchers.IO) {
                serviceState.mapNotNull { it.selected?.let { index -> it.services.getOrNull(index) } }
                    .collectLatest { service ->
                        Log.d("MainViewModel", "Service: $service")
                    if (serviceState.value.providerState == ProviderState.IDLE) {
                        _homeUiState.value.location?.let { location ->
                            directionsUseCase(
                                location.toLocationModel() to service.serviceLocation
                            ).onSuccess {
                                val route = it.routes.minByOrNull { it.duration }
                                _homeUiState.update {
                                    it.copy(directions = route)
                                }
                            }
                        }
                    }
                }
            }
            launch(Dispatchers.IO) {
                serviceState.mapNotNull { it.serviceModel }.collectLatest { service ->
                    if (serviceState.value.providerState == ProviderState.NAVIGATING) {
                        onAction(Action.LocationUpdate)
                        _homeUiState.value.location?.let { location ->
                            directionsUseCase(
                                location.toLocationModel() to service.serviceLocation
                            ).onSuccess { result ->
                                _homeUiState.update {
                                    it.copy(
                                        directions = result.routes.minByOrNull { it.duration }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            launch(Dispatchers.IO) {
                _homeUiState.map { it.location }.collectLatest {
                    onAction(Action.LocationUpdate)
                }
            }
        }
    }

    fun onAction(action: Action) {
        when(action) {
            Action.HideFinishDialog -> {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceManager.onAction(Finish)
                }
            }
            is Action.SetLocation -> {
                _homeUiState.update {
                    it.copy(location = action.location)
                }
            }

            Action.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    sendEvent(ExitToAuthActivity)
                }
            }

            is Action.AcceptService -> {
                _homeUiState.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    serviceManager.onAction(Accept(_homeUiState.value.location?.toLocationModel()))
                    _homeUiState.value.location?.let { location ->
                        serviceManager.onAction(
                            LocationUpdate(location.toLocationModel())
                        )
                    }
                    _homeUiState.update {
                        it.copy(loading = false)
                    }
                }
            }
            is Action.SelectService -> {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceManager.onAction(SelectService(action.index))
                }
            }
            Action.UnSelectService -> {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceManager.onAction(UnSelectService)
                }
            }
            is Action.SendEvent -> {
                sendEvent(action.event)
            }
            Action.Arrived -> {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceManager.onAction(Arrived)
                }
            }
            Action.LocationUpdate -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (serviceManager.service.value.providerState == ProviderState.NAVIGATING)
                        serviceManager.service.value.serviceModel?.let { service ->
                            _homeUiState.value.location?.let { location ->
                                serviceManager.onAction(
                                    LocationUpdate(location.toLocationModel())
                                )
                            }
                        }
                }
            }
            Action.RemoveRoutes -> {
                Log.i("MainViewModel", "Action.RemoveRoutes")
                _homeUiState.update {
                    it.copy(directions = null, location = null)
                }
            }
            Action.RefreshUser -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _homeUiState.update {
                        it.copy(loading = true)
                    }
                    refreshUseCase()
                    _homeUiState.update {
                        it.copy(loading = false)
                    }
                }
            }
            Action.SendMessage -> {
                _homeUiState.update {
                    it.copy(message = "")
                }
            }
            is Action.SetMessage -> {
                _homeUiState.update {
                    it.copy(message = action.message)
                }
            }
            Action.FetchServices -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _homeUiState.update {
                        it.copy(servicesLoading = true)
                    }
                    fetchServices().onSuccess { service ->
                        _servicesHistory.value = service
                        _homeUiState.update {
                            it.copy(servicesLoading = false)
                        }
                    }.onError {
                        Log.e("MainViewModel", "Error fetching services: ${it.text}")
                        _homeUiState.update {
                            it.copy(servicesLoading = false)
                        }
                    }
                }
            }
        }
    }
}