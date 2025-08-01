package io.yubicolabs.pawskey

import android.util.Log
import com.yubico.yubikit.android.YubiKitManager
import com.yubico.yubikit.android.transport.nfc.NfcConfiguration
import com.yubico.yubikit.android.transport.nfc.NfcYubiKeyDevice
import com.yubico.yubikit.android.transport.usb.UsbConfiguration
import com.yubico.yubikit.android.transport.usb.UsbYubiKeyDevice
import com.yubico.yubikit.core.YubiKeyDevice
import com.yubico.yubikit.core.util.Callback
import com.yubico.yubikit.fido.client.BasicWebAuthnClient
import com.yubico.yubikit.fido.ctap.Ctap2Session
import kotlinx.coroutines.delay
import javax.inject.Inject


class PassKeyService @Inject constructor(
    private val yubico: YubiKitManager,
    private val activityProvider: ActivityProvider,
) {
    interface Listener {
        operator fun invoke(webAuthnClient: BasicWebAuthnClient)
    }

    private val usbConfig: UsbConfiguration = UsbConfiguration().handlePermissions(true)
    private val usbListener: Callback<UsbYubiKeyDevice> = Callback<UsbYubiKeyDevice> { device ->
        onDeviceConnected(device)
    }

    private val nfcConfig: NfcConfiguration = NfcConfiguration()
        .disableNfcDiscoverySound(false)
        .handleUnavailableNfc(true)
        .timeout(3_000)
        .skipNdefCheck(false)
    private val nfcListener: Callback<NfcYubiKeyDevice> = Callback<NfcYubiKeyDevice> { device ->
        onDeviceConnected(device)
    }

    private val authnClients: MutableList<BasicWebAuthnClient> = mutableListOf()
    private val clientListeners: MutableList<Listener> = mutableListOf()

    fun startDeviceDiscovery() {
        yubico.startUsbDiscovery(
            usbConfig,
            usbListener
        )

        // FIXME ACTIVITY INJECTION

        yubico.startNfcDiscovery(
            nfcConfig,
            activityProvider.getActivity(),
            nfcListener
        )
    }

    private fun onDeviceConnected(device: YubiKeyDevice) {
        Log.i(tagForLog, "Connected to device $device.")

        Ctap2Session.create(device) { result ->
            if (!result.isSuccess) {
                Log.e(tagForLog, "Couldn't create session for device $device: ${result.error}")
            } else {
                val session = result.value
                val authnClient = BasicWebAuthnClient(session)
                Log.i(tagForLog, "Created session: $session and authnclient $authnClient. Informing all listeners.")

                authnClients.add(authnClient)
                informListeners(authnClient)
            }
        }
    }

    /**
     * Get informed when a new device gets connected and we were able to establish a session for a webauthn client.
     *
     * @param listener the callback invoked with every new session created
     * @param updateWithCurrentState when set to true calls the new listener with the current active clients
     */
    fun addWebauthnClientListener(listener: Listener, updateWithCurrentState: Boolean): Boolean {
        if (updateWithCurrentState) {
            authnClients.forEach { listener(it) }
        }

        return clientListeners.add(listener)
    }

    /**
     * Remove a listener for webauthn clients once not needed anymore.
     */
    fun removeClientListener(listener: Listener) = clientListeners.remove(listener)

    fun forAllWebAuthnClients(block: BasicWebAuthnClient.() -> Unit): Int {
        authnClients.forEach {
            it.block()
        }

        return authnClients.size
    }

    val connectedKeyCount: Int
        get() = authnClients.size

    private fun informListeners(webAuthnClient: BasicWebAuthnClient) {
        clientListeners.forEach { it(webAuthnClient) }
    }

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

    fun stopDeviceDiscovery() {
        yubico.stopUsbDiscovery()
        yubico.stopNfcDiscovery(activityProvider.getActivity())

        clientListeners.clear()
    }
}


private val <T, TH : Throwable> com.yubico.yubikit.core.util.Result<T, TH>.error: Throwable
    get() = try {
        this.value
        IllegalStateException("No error found.")
    } catch (th: Throwable) {
        th
    }