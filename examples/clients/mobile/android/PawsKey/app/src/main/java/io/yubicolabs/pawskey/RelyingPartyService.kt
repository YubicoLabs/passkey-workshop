package io.yubicolabs.pawskey

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject

@Serializable
data class Status(
    @SerialName("status")
    val status: String
)

interface RelyingPartyHttpService {
    @GET("v1/status")
    suspend fun getStatus(): Response<Status>
}

class RelyingPartyService @Inject constructor(
    private val httpService: RelyingPartyHttpService
) {
    /**
     * Status: Is the server alive and kicking?
     *
     * @throws retrofit2.HttpException on error code response (not 2xx http codes)
     * @throws java.net.SocketTimeoutException on timeout
     */
    suspend fun getStatus(): Status {
        val response = httpService.getStatus()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            return body
        } else {
            throw HttpException(response)
        }
    }
}
