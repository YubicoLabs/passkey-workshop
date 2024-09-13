package io.yubicolabs.pawskey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.yubicolabs.pawskey.Message.KeysFound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject


sealed class Message {
    data object ServerOkay : Message()

    data object ServerNotOkay : Message()

    data object ServerTimeout : Message()

    data class AttestationOptionsReceived(
        val id: String,
        val key: String,
    ) : Message()

    data class KeysFound(
        val keys: List<String>
    ) : Message()

    data class ServerError(
        val message: String
    ) : Message()
}

data class UiState(
    val userMessages: List<Message> = emptyList()
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val relyingPartyService: RelyingPartyService,
    private val passKeyService: PassKeyService,
    private val clipboard: ClipboardProvider,
    private val json: Json,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState()
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun checkApiStatus() {
        viewModelScope.launch {
            val message = try {
                val result = relyingPartyService.getStatus()

                if (result) {
                    Message.ServerOkay
                } else {
                    Message.ServerNotOkay
                }

            } catch (exception: SocketTimeoutException) {
                Message.ServerTimeout
            } catch (exception: HttpException) {
                Message.ServerError("${exception.code()}: ${exception.message()}")
            }

            showUserMessage(message)
        }
    }

    fun registerUser() {
        viewModelScope.launch {
            val message = try {
                val result = relyingPartyService.getAttestationOptions(
                    userName = "User Name"
                )

                if (result.requestId.isNotBlank()) {
                    Message.AttestationOptionsReceived(
                        result.requestId,
                        json.encodeToString(result.publicKey),
                    )

                    // keys.create(resul) --> use keys to register requested creds.
                    // report back if successful
                } else {
                    Message.ServerNotOkay
                }
            } catch (exception: SocketTimeoutException) {
                Message.ServerTimeout
            } catch (exception: HttpException) {
                Message.ServerError("${exception.code()}: ${exception.message()}")
            }

            showUserMessage(message)
        }
    }

    private fun showUserMessage(
        message: Message
    ) {
        _uiState.update {
            it.copy(userMessages = it.userMessages + message)
        }
    }

    fun getConnectedKeys() {
        viewModelScope.launch {
            val result = passKeyService.findConnectedKeys()

            showUserMessage(
                KeysFound(result?.map { it.id } ?: emptyList())
            )

        }
    }

    fun copyToClipboard(message: String) {
        clipboard.setContent(message)
    }
}