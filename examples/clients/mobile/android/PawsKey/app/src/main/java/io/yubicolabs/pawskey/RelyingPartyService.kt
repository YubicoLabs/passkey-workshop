package io.yubicolabs.pawskey

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import javax.inject.Inject

@Serializable
data class Status(
    @SerialName("status")
    val status: String
)

@Serializable
data class AttestationOptionsRequest(
    val userName: String,
    val displayName: String,
    val authenticatorSelection: AuthenticatorSelection,
    val attestation: String? = null,
) {
    @Serializable
    data class AuthenticatorSelection(
        val residentKey: String,
        val authenticatorAttachment: String? = null,
        val userVerification: String? = null,
    )
}

@Serializable
data class AttestationOptionsResponse(
    val requestId: String,
    val publicKey: PublicKey,
) {
    @Serializable
    data class PublicKey(
        val rp: RelyingParty,
        val user: User,
        val challenge: String,
        val pubKeyCredParams: List<CredParam>,
        val timeout: Long,
        val excludeCredentials: List<String>,
        val authenticatorSelection: AttestationOptionsRequest.AuthenticatorSelection,
        val attestation: String
    ) {
        @Serializable
        data class RelyingParty(
            val name: String,
            val id: String,
        )

        @Serializable
        data class User(
            val id: String,
            val name: String,
            val displayName: String,
        )

        @Serializable
        data class CredParam(
            val type: String,
            val alg: Int,
        )
    }
}

interface RelyingPartyHttpService {
    @GET("v1/status")
    suspend fun getStatus(): Response<Status>

    @POST("v1/attestation/options")
    suspend fun getAttestationOptions(
        @Body options: AttestationOptionsRequest
    ): Response<AttestationOptionsResponse>
}

class RelyingPartyService @Inject constructor(
    private val httpService: RelyingPartyHttpService,
) {
    /**
     * Status: Is the server alive and kicking?
     *
     * @throws retrofit2.HttpException on error code response (not 2xx http codes)
     * @throws java.net.SocketTimeoutException on timeout
     */
    suspend fun getStatus(): Boolean {
        val response = httpService.getStatus()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            return body.status == "ok"
        } else {
            val ex = HttpException(response)
            Log.e(tagForLog, "Status endpoint returned error. Response was $response.", ex)
            throw ex
        }
    }

    suspend fun getAttestationOptions(
        userName: String
    ): AttestationOptionsResponse {
        val response = httpService.getAttestationOptions(
            AttestationOptionsRequest(
                userName = userName,
                displayName = userName,
                authenticatorSelection = AttestationOptionsRequest.AuthenticatorSelection(
                    residentKey = "preferred"
                )
            )
        )

        val body = response.body()

        if (response.isSuccessful && body != null) {
            return body
        } else {
            throw HttpException(response)
        }
    }
}

