package io.yubicolabs.pawskey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
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
                        checkServer = vm::checkApiStatus
                    )
                }
            }
        }
    }
}

@Composable
fun PawskeyUi(
    modifier: Modifier = Modifier,
    uiState: UiState,
    checkKeys: () -> Unit,
    checkServer: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        if (uiState.apiConnected != null) {
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = "${if (uiState.apiConnected == true) "Successfully" else "Not"} connected to ${uiState.relyingPartyId}."
            )
        }

        if ((uiState.connectedKeys ?: emptyList()).isNotEmpty()) {
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = "Found ${uiState.connectedKeys!!.size} key(s)."
            )
        }

        Row {
            Spacer(modifier = modifier.weight(1.0f))
            Button(onClick = checkKeys) {
                Text(
                    text = "Keys"
                )
            }
            Spacer(modifier = modifier.weight(1.0f))
            Button(onClick = checkServer) {
                Text(
                    text = "Server"
                )
            }
            Spacer(modifier = modifier.weight(1.0f))
        }
    }
}
