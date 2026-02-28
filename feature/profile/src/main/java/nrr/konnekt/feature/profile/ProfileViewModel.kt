package nrr.konnekt.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.model.UserEdit
import nrr.konnekt.core.domain.usecase.UpdateProfileUseCase
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    authentication: Authentication,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    internal val currentUser = _currentUser.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            authentication
                .loggedInUser
                .firstOrNull()
                ?.let {
                    _currentUser.value = it
                }
        }
    }

    internal fun updateProfile(userEdit: UserEdit) {
        viewModelScope.launch {
            when (val res = updateProfileUseCase(userEdit)) {
                is Result.Success -> _currentUser.value = res.data
                is Result.Error -> _events.emit(
                    value = UiEvent.ShowSnackbar(
                        message = "Fail to update profile."
                    )
                )
            }
        }
    }
}