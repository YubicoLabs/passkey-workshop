package io.yubicolabs.pawskey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject


data class KeyUi(
    val name: String
)

sealed class Message {
    data object ServerOkay : Message()

    data class ServerNotOkay(
        val status: String
    ) : Message()

    data object ServerTimeout : Message()

    data class ServerError(
        val message: String
    ) : Message()
}

data class UiState(
    val connectedKeys: List<KeyUi>? = null,

    val userMessages: List<Message> = emptyList()
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
            val message = try {
                val result = relyingPartyService.getStatus()

                if (result.status == "ok") {
                    Message.ServerOkay
                } else {
                    Message.ServerNotOkay(result.status)
                }

            } catch (exception: SocketTimeoutException) {
                Message.ServerTimeout
            } catch (exception: HttpException) {
                Message.ServerError("${exception.code()}: ${exception.message()}")
            }

            showUserMessage(message)
        }
    }

    private suspend fun showUserMessage(
        message: Message,
        displaySeconds: Double = 3.0,
    ) {
        _uiState.update {
            it.copy(userMessages = it.userMessages + message)
        }

        delay((displaySeconds * 1000).toLong())

        _uiState.update {
            it.copy(
                userMessages = it.userMessages.except(message)
            )
        }
    }

    private fun <T> List<T>.except(element: T) = filter { it != element }

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