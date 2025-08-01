package io.yubicolabs.pawskey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dagger.hilt.android.AndroidEntryPoint
import io.yubicolabs.pawskey.Message.AttestationOptionsReceived
import io.yubicolabs.pawskey.Message.KeysFound
import io.yubicolabs.pawskey.Message.PublicKeyGenerated
import io.yubicolabs.pawskey.Message.ServerOkay
import io.yubicolabs.pawskey.UserInteraction.AttestationError
import io.yubicolabs.pawskey.UserInteraction.ConnectKey
import io.yubicolabs.pawskey.UserInteraction.CreationError
import io.yubicolabs.pawskey.UserInteraction.EnterPin
import io.yubicolabs.pawskey.UserInteraction.UserNameWrong
import io.yubicolabs.pawskey.UserInteraction.WaitForRP
import io.yubicolabs.pawskey.ui.theme.PawsKeyTheme
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val log: Logger = LoggerFactory.getLogger(MainActivity::class.java)
        log.info("hello world")


        /// TODO Don't look to closely: NFC Needs the main activity.
        PawskeyApplication.instance.mainActivity = this

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
                    Box {
                        PawskeyUi(
                            modifier = Modifier.padding(innerPadding),
                            uiState = state,
                            checkServer = vm::checkApiStatus,
                            register = vm::registerUser,
                            copyToClipboard = { message ->
                                vm.copyToClipboard(message.toString())
                            },
                            delete = { message ->
                                vm.deleteMessage(message)
                            }
                        )

                        state.userInteractionNeeded?.let { interaction ->
                            UserInteractionModal(
                                interaction,
                                setPin = { vm.pin = it },
                                cancelled = vm::interactionCancelled,
                                copy = { vm.copyToClipboard(it) },
                                cancelAllInteractions = vm::cancelAllInteractions
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun UserInteractionModal(
    interaction: UserInteraction = EnterPin,
    setPin: (pin: String) -> Unit = {},
    cancelled: () -> Unit = {},
    copy: (message: String) -> Unit = {},
    cancelAllInteractions: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (interaction) {
            is EnterPin -> EnterPinModal(
                success = setPin,
                cancelled = cancelled
            )

            is WaitForRP -> WaitForRPModal(
                cancelled = cancelled
            )

            is UserNameWrong -> ErrorMessage(
                message = stringResource(id = R.string.error_wrong_user_name),
                confirm = cancelled,
                dismiss = cancelled,
                copy = copy,
            )

            is AttestationError -> ErrorMessage(
                message = stringResource(id = R.string.error_no_attestatation),
                confirm = cancelled,
                dismiss = cancelled,
                copy = copy
            )

            is CreationError -> ErrorMessage(
                message = stringResource(id = R.string.error_not_created),
                confirm = cancelled,
                dismiss = cancelled,
                copy = copy
            )

            is ConnectKey -> ConnectKeyMessage(cancelled = cancelAllInteractions)
        }

    }
}

@Composable
@Preview
private fun ConnectKeyMessage(
    cancelled: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    text = "Please connect a key."
                )
                CircularProgressIndicator()
                Row {
                    Spacer(modifier = Modifier.weight(1.0f))
                    Button(onClick = cancelled) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun WaitForRPModal(
    cancelled: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    text = "Waiting for Relying Party."
                )
                CircularProgressIndicator()
                Row {
                    Spacer(modifier = Modifier.weight(1.0f))
                    Button(onClick = cancelled) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun ErrorMessage(
    message: String = "Something went wrong.",
    confirm: () -> Unit = {},
    dismiss: () -> Unit = {},
    copy: (message: String) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            Button(onClick = confirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        title = { Text(stringResource(id = R.string.dialog_title_error)) },
        text = { Text(message) },
        dismissButton = {
            IconButton(onClick = { copy(message) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = null
                )
            }
        },
    )
}

@Composable
@Preview
private fun EnterPinModal(
    cancelled: () -> Unit = {},
    success: (pin: String) -> Unit = {}
) {
    // TODO Alert Dialog?
    // TODO CUSTOM VM?
    var pin: String by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = cancelled,
        confirmButton = {
            Button(onClick = { success(pin) }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        title = { Text(stringResource(id = R.string.dialog_title_pin_needed)) },
        text = {
            Column {
                Text(
                    modifier = Modifier.padding(vertical = 5.dp),
                    text = stringResource(R.string.pin_dialog_need)
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = pin,
                    label = { Text("Pin") },
                    onValueChange = { pin = it }
                )
            }
        },
        dismissButton = {
            Button(onClick = cancelled) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}

@Composable
@Preview
fun PawskeyUi(
    modifier: Modifier = Modifier,
    uiState: UiState = UiState(
        userMessages = listOf<Message>(
            ServerOkay(),
            PublicKeyGenerated("id", "type"),
            AttestationOptionsReceived("id", "key")
        )
    ),
    checkServer: () -> Unit = {},
    register: (userName: String, pin: String) -> Unit = { _, _ ->
    },
    copyToClipboard: (message: Message) -> Unit = {},
    delete: (message: Message) -> Unit = {},
) {
    var userName: String by remember { mutableStateOf("Android Test User") }
    var pin: String by remember { mutableStateOf("123456") }

    Column(
        modifier = modifier
    ) {
        Card {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = userName,
                    label = {
                        Text(
                            stringResource(id = R.string.user_name_label)
                        )
                    },
                    onValueChange = { userName = it }
                )
                TextField(
                    value = pin,
                    label = {
                        Text(
                            stringResource(id = R.string.pin_label)
                        )
                    },
                    onValueChange = { pin = it }
                )
                Row {
                    Button(onClick = checkServer) {
                        Text(
                            text = "Status"
                        )
                    }
                    Button(onClick = { register(userName, pin) }) {
                        Text(
                            text = "Register"
                        )
                    }
                }
            }
        }

        Card {
            LazyColumn {
                items(uiState.userMessages.reversed()) { message ->
                    UserMessage(
                        message,
                        { copyToClipboard(message) },
                        { delete(message) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun UserMessage(
    message: Message = ServerOkay(),
    copyToClipboard: () -> Unit = {},
    delete: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1.0f),
            style = MaterialTheme.typography.bodyLarge,
            text = message.toString()
        )
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable { delete() },
            painter = painterResource(id = R.drawable.baseline_outline_delete_24),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            modifier = Modifier
                .size(20.dp)
                .clickable { copyToClipboard() },
            painter = painterResource(id = R.drawable.baseline_content_copy_24),
            contentDescription = null
        )
    }
}
