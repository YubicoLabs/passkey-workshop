package io.yubicolabs.pawskey

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException


class RelyingPartyServiceTest {
    @Test
    fun getStatus() = runTest {
        val httpService = object : RelyingPartyHttpService {
            override suspend fun getStatus(): Response<Status> = Response.success(Status("OK"))
        }

        val rpService = RelyingPartyService(httpService)
        assertEquals("OK", rpService.getStatus().status)
    }

    @Test
    fun getStatusIfErrorOnServer() = runTest {
        val httpService = object : RelyingPartyHttpService {
            override suspend fun getStatus(): Response<Status> = throw HttpException(
                Response.error<String>(
                    400,
                    ResponseBody.create(null, "TEST SUPPOSED TO FAIL")
                )
            )
        }

        val rpService = RelyingPartyService(httpService)
        assertThrows(HttpException::class.java) {
            runBlocking {
                rpService.getStatus().status
            }
        }
    }

    @Test
    fun getTimeoutError() = runTest {
        val httpService = object : RelyingPartyHttpService {
            override suspend fun getStatus(): Response<Status> = throw SocketTimeoutException()
        }

        // TODO: Make meaningful

        val rpService = RelyingPartyService(httpService)
        assertThrows(SocketTimeoutException::class.java) {
            runBlocking {
                rpService.getStatus()
            }
        }
    }
}