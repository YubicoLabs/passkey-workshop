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
            override suspend fun getStatus(): Response<Status> = Response.success(Status("ok"))
            override suspend fun getAttestationOptions(options: AttestationOptionsRequest): Response<AttestationOptionsResponse> =
                Response.success(null)
        }

        val rpService = RelyingPartyService(httpService)
        assertEquals(true, rpService.getStatus())
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

            override suspend fun getAttestationOptions(options: AttestationOptionsRequest): Response<AttestationOptionsResponse> =
                Response.success(null)
        }

        val rpService = RelyingPartyService(httpService)
        assertThrows(HttpException::class.java) {
            runBlocking {
                rpService.getStatus()
            }
        }
    }

    @Test
    fun getTimeoutError() = runTest {
        val httpService = object : RelyingPartyHttpService {
            override suspend fun getStatus(): Response<Status> = throw SocketTimeoutException()
            override suspend fun getAttestationOptions(options: AttestationOptionsRequest): Response<AttestationOptionsResponse> =
                Response.success(null)
        }

        // TODO: Make meaningful

        val rpService = RelyingPartyService(httpService)
        assertThrows(SocketTimeoutException::class.java) {
            runBlocking {
                rpService.getStatus()
            }
        }
    }

    @Test
    fun getAttestationOptions() = runTest {
        val httpService = object : RelyingPartyHttpService {
            override suspend fun getStatus(): Response<Status> = Response.success(Status("ok"))
            override suspend fun getAttestationOptions(options: AttestationOptionsRequest): Response<AttestationOptionsResponse> =
                Response.success(
                    AttestationOptionsResponse(
                        "RID",
                        AttestationOptionsResponse.PublicKey(
                            AttestationOptionsResponse.PublicKey.RelyingParty(
                                "relying party name",
                                "relying party id"
                            ),
                            AttestationOptionsResponse.PublicKey.User(
                                "userId",
                                "userName",
                                "User Name Display",
                            ),
                            "Challenge String",
                            listOf(
                                AttestationOptionsResponse.PublicKey.CredParam(
                                    "type",
                                    2134,
                                )
                            ),
                            1234145,
                            listOf(),
                            AttestationOptionsRequest.AuthenticatorSelection(
                                "residentKey",
                                "authAttach",
                                "userverify",
                            ),
                            "attestation",
                        )
                    )
                )
        }

        val rpService = RelyingPartyService(httpService)
        assertEquals(
            "attestation",
            rpService.getAttestationOptions(
                "userName"
            ).publicKey.attestation
        )
    }
}