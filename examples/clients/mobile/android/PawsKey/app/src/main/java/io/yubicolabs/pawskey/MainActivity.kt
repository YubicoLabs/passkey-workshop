package io.yubicolabs.pawskey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import io.yubicolabs.pawskey.Message.AttestationOptionsReceived
import io.yubicolabs.pawskey.Message.KeysFound
import io.yubicolabs.pawskey.Message.ServerError
import io.yubicolabs.pawskey.Message.ServerNotOkay
import io.yubicolabs.pawskey.Message.ServerOkay
import io.yubicolabs.pawskey.Message.ServerTimeout
import io.yubicolabs.pawskey.ui.theme.PawsKeyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawsKeyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name)
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    val state by vm.uiState.collectAsState()
                    PawskeyUi(
                        modifier = Modifier.padding(innerPadding),
                        uiState = state,
                        checkKeys = vm::getConnectedKeys,
                        checkServer = vm::checkApiStatus,
                        register = vm::registerUser,
                        copyToClipboard = vm::copyToClipboard,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PawskeyUi(
    modifier: Modifier = Modifier,
    uiState: UiState = UiState(
        userMessages = listOf(ServerOkay, KeysFound(listOf("a", "b")))
    ),
    checkKeys: () -> Unit = {},
    checkServer: () -> Unit = {},
    register: () -> Unit = {},
    copyToClipboard: (message: String) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = checkKeys) {
                    Text(
                        text = "Keys"
                    )
                }
                Button(onClick = checkServer) {
                    Text(
                        text = "Status"
                    )
                }
                Button(onClick = register) {
                    Text(
                        text = "Register"
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
        }

        Card {
            LazyColumn {
                items(uiState.userMessages) {
                    val message = when (it) {
                        is ServerOkay ->
                            stringResource(id = R.string.message_server_okay, BuildConfig.RELYING_PARTY_ID)

                        is AttestationOptionsReceived ->
                            stringResource(id = R.string.message_server_attestation_received, it.id, it.key)

                        is KeysFound ->
                            stringResource(id = R.string.message_keys_found, it.keys.joinToString())

                        is ServerNotOkay -> stringResource(id = R.string.message_server_not_okay)
                        is ServerError -> stringResource(id = R.string.message_server_error, it.message)
                        is ServerTimeout -> stringResource(id = R.string.message_server_timeout)
                    }

                    Row(
                        modifier = Modifier
                            .clickable { copyToClipboard(message) }
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1.0f),
                            style = MaterialTheme.typography.bodySmall,
                            text = message
                        )
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(id = R.drawable.baseline_content_copy_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
