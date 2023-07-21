import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PromptDialog(
    onConfirm: (baseUrl: String, token: String) -> Unit,
    onCancel: () -> Unit
) {
    val defaultBaseUrl = "https://cloud.almostflip.com"

    var baseUrl by remember { mutableStateOf(defaultBaseUrl) }
    var token by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onCancel) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Base URL")
            TextField(value = baseUrl, onValueChange = { baseUrl = it })

            Box(modifier = Modifier.height(64.dp)) {
                TextField(
                    value = token,
                    onValueChange = { token = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(onClick = { onConfirm(baseUrl, token) }) {
                Text("OK")
            }
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

