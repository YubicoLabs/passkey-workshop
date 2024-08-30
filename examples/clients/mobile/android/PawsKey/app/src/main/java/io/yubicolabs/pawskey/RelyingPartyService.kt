package io.yubicolabs.pawskey

import kotlinx.coroutines.delay

class RelyingPartyService() {
    data class StatusResponse(
        val status: String
    )

    suspend fun getStatus(): StatusResponse {
        // TODO replace with actual api call
        delay(500)
        return StatusResponse("TODO")
    }
}
