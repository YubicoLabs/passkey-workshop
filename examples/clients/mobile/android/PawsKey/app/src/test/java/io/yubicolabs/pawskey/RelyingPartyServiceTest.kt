package io.yubicolabs.pawskey

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test


class RelyingPartyServiceTest {

    @Test
    fun getStatus() = runTest {
        assertEquals("OK", RelyingPartyService().getStatus().status)
    }
}