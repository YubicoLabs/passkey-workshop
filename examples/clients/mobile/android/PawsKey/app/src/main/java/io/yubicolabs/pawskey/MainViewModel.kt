package io.yubicolabs.pawskey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class KeyUi(
    val name: String
)

data class UiState(
    val apiConnected: Boolean? = null,
    val connectedKeys: List<KeyUi>? = null,

    val relyingPartyId: String = BuildConfig.RELYING_PARTY_ID,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val relyingPartyService: RelyingPartyService,
    private val passKeyService: PassKeyService,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState()
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun checkApiStatus() {
        viewModelScope.launch {
            val result = relyingPartyService.getStatus()

            _uiState.update { it.copy(apiConnected = result.status == "ok") }
        }
    }

    fun getConnectedKeys() {
        viewModelScope.launch {
            val result = passKeyService.findConnectedKeys()

            _uiState.update {
                it.copy(connectedKeys = (result ?: emptyList()).map {
                    KeyUi(it.id)
                })
            }
        }
    }
}