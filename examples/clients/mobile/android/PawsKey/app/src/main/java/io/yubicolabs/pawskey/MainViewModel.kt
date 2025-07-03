@file:OptIn(ExperimentalUuidApi::class)

package io.yubicolabs.pawskey

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yubico.yubikit.fido.client.BasicWebAuthnClient
import com.yubico.yubikit.fido.webauthn.AuthenticatorSelectionCriteria
import com.yubico.yubikit.fido.webauthn.PublicKeyCredential
import com.yubico.yubikit.fido.webauthn.PublicKeyCredentialCreationOptions
import com.yubico.yubikit.fido.webauthn.PublicKeyCredentialDescriptor
import com.yubico.yubikit.fido.webauthn.PublicKeyCredentialParameters
import com.yubico.yubikit.fido.webauthn.PublicKeyCredentialRpEntity
import com.yubico.yubikit.fido.webauthn.PublicKeyCredentialUserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.yubicolabs.pawskey.Message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class UserInteraction {
    data object WaitForRP : UserInteraction()

    data object EnterPin : UserInteraction()

    data object UserNameWrong : UserInteraction()

    data object ConnectKey : UserInteraction()

    data object AttestationError : UserInteraction()

    data object CreationError : UserInteraction()
}

sealed class Message(
    val uuid: Uuid = Uuid.random()
) {
    data class ServerOkay(
        val additionalInfo: String? = null
    ) : Message()

    data class ServerNotOkay(
        val th: Throwable? = null
    ) : Message()

    data class ServerNotConnected(
        val th: Throwable
    ) : Message()

    data class ServerTimeout(
        val additionalInfo: String? = null
    ) : Message()

    data class UserNameWrong(
        val additionalInfo: String? = null
    ) : Message()

    data class PublicKeyGenerated(
        val id: String,
        val type: String,
    ) : Message()

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

    data class KeyCreationFailure(
        val th: Throwable
    ) : Message()
}

data class UiState(
    val userMessages: List<Message> = emptyList(),
    val userInteractionNeeded: UserInteraction? = null,
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

    var pin: String? = null

    var userName: String? = null
    var attestation: AttestationOptionsResponse? = null
    var publicKey: PublicKeyCredential? = null

    fun checkApiStatus() {
        showUserInteraction(
            UserInteraction.WaitForRP
        )

        viewModelScope.launch {
            val message = try {
                val result = relyingPartyService.getStatus()

                if (result) {
                    ServerOkay()
                } else {
                    ServerNotOkay()
                }

            } catch (_: SocketTimeoutException) {
                ServerTimeout()
            } catch (exception: HttpException) {
                ServerError("${exception.code()}: ${exception.message()}")
            }

            showUserInteraction(null)
            showUserMessage(message)
        }
    }

    fun registerUser(userName: String, pin: String) {
        if (userName.isEmpty()) {
            showUserMessage(UserNameWrong())
            showUserInteraction(UserInteraction.UserNameWrong)
        } else if (pin.isEmpty()) {
            showUserInteraction(UserInteraction.EnterPin)
        } else {
            this.userName = userName
            this.pin = pin

            viewModelScope.launch(Dispatchers.IO) {
                showUserInteraction(UserInteraction.WaitForRP)
                attestation = getAttestationOptionsFromRP()

                if (attestation == null) {
                    showUserInteraction(UserInteraction.AttestationError)
                } else {
                    createKey(attestation!!)
                }
            }
        }
    }

    private fun createKey(attestationOptions: AttestationOptionsResponse) {
        if (userName.isNullOrEmpty()) {
            showUserInteraction(UserInteraction.UserNameWrong)
        } else if (pin.isNullOrEmpty()) {
            showUserInteraction(UserInteraction.EnterPin)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.update {
                    it.copy(userInteractionNeeded = UserInteraction.ConnectKey)
                }

                passKeyService.addWebauthnClientListener(
                    object : PassKeyService.Listener {
                        override fun invoke(webAuthnClient: BasicWebAuthnClient) {
                            showUserInteraction(UserInteraction.WaitForRP)

                            createKeyOnDevice(attestationOptions, webAuthnClient)
                        }
                    },
                    updateWithCurrentState = true,
                )
                passKeyService.startDeviceDiscovery()
            }
        }
    }

    private fun PassKeyService.Listener.createKeyOnDevice(
        attestationOptions: AttestationOptionsResponse,
        webAuthnClient: BasicWebAuthnClient
    ) {
        try {
            Log.i(tagForLog, "Client $this found.")

            val clientDataJson = attestationOptions.toClientDataJsonByteArray()
            val options = attestationOptions.publicKey.toOptions()
            val effectiveDomain = attestationOptions.publicKey.rp.id

            publicKey = webAuthnClient.makeCredential(
                clientDataJson,
                options,
                effectiveDomain,
                pin!!.toCharArray(),
                null,
                null,
            )

            if (publicKey != null) {
                showUserInteraction(null)
                showUserMessage(PublicKeyGenerated(publicKey!!.id, publicKey!!.type))
            } else {
                showUserInteraction(UserInteraction.CreationError)
            }
        } catch (throwable: Throwable) {
            val message = KeyCreationFailure(throwable)
            Log.e(tagForLog, message.toString(), throwable)

            attestation = null

            showUserMessage(message)
            showUserInteraction(null)
        }
    }

    private suspend fun getAttestationOptionsFromRP(): AttestationOptionsResponse? {
        return try {
            val result = relyingPartyService.getAttestationOptions(
                userName ?: "Not set"
            )

            if (result.requestId.isNotBlank()) {
                val hint = StringBuilder()
                val user = result.publicKey.user
                hint.append(json.encodeToString(user))

                showUserMessage(
                    AttestationOptionsReceived(
                        result.requestId,
                        result.publicKey.toString(),
                    )
                )
                result
            } else {
                showUserMessage(ServerNotOkay())
                null
            }
        } catch (e: ConnectException) {
            showUserMessage(ServerNotConnected(e))
            null
        } catch (_: SocketTimeoutException) {
            showUserMessage(ServerTimeout())
            null
        } catch (exception: HttpException) {
            showUserMessage(ServerNotOkay(exception))
            null
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun showUserMessage(
        message: Message
    ) {
        _uiState.update {
            it.copy(userMessages = it.userMessages + message)
        }
    }

    private fun showUserInteraction(
        interaction: UserInteraction?
    ) {
        _uiState.update {
            it.copy(userInteractionNeeded = interaction)
        }
    }

    fun copyToClipboard(message: String) {
        clipboard.setContent(message)
    }

    fun deleteMessage(message: Message) {
        _uiState.update {
            it.copy(
                userMessages = it.userMessages.filter {
                    it.uuid != message.uuid
                }
            )
        }
    }

    fun interactionCancelled() {
        _uiState.update {
            it.copy(
                userInteractionNeeded = null
            )
        }
    }

    fun cancelAllInteractions() {
        passKeyService.stopDeviceDiscovery()

        _uiState.update {
            it.copy(
                userInteractionNeeded = null
            )
        }
    }
}

private fun AttestationOptionsResponse.toClientDataJsonByteArray(): ByteArray = """
        { 
          type: "webauthn.create",
          challenge: "${publicKey.challenge}",
          origin: "${publicKey.rp.id}"
        }
    """.trimIndent().trimIndent().toByteArray()

private fun AttestationOptionsResponse.PublicKey.toOptions(): PublicKeyCredentialCreationOptions =
    PublicKeyCredentialCreationOptions(
        PublicKeyCredentialRpEntity(
            rp.name,
            rp.id
        ),
        PublicKeyCredentialUserEntity(
            user.name,
            user.id.toByteArray(),
            user.displayName
        ),
        challenge.toByteArray(),
        pubKeyCredParams.map {
            PublicKeyCredentialParameters(
                it.type,
                it.alg
            )
        },
        180000,
        excludeCredentials.map {
            PublicKeyCredentialDescriptor(
                it.type,
                it.id.toByteArray()
            )
        },
        AuthenticatorSelectionCriteria(
            authenticatorSelection.authenticatorAttachment,
            authenticatorSelection.residentKey,
            authenticatorSelection.userVerification,
        ),
        attestation,
        null
    )
