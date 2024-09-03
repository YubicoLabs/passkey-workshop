package io.yubicolabs.pawskey

import kotlinx.coroutines.delay

class PassKeyService {
    data class Key(
        val id: String,
    )

    suspend fun findConnectedKeys(): List<Key>? {
        // TODO Replace with sdk / platform impl

        delay(500)

        return listOf(
            Key("mocked")
        )
    }
}
