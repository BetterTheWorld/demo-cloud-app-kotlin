import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*

import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember


import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.core.content.ContextCompat.startActivity
import java.net.MalformedURLException
import java.net.URL


@Composable
fun WebViewComponent(
    baseUrl: String,
    token: String,
) {
    val url = "$baseUrl/?token=$token"
    var showDialog by remember { mutableStateOf(false) }
    val webViewState = remember { mutableStateOf<WebView?>(null) }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(Unit) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webViewState.value?.canGoBack() == true) {
                    webViewState.value?.goBack()
                } else {
                    // WebView cannot go back, handle the back press event here
                }
            }
        }
        onBackPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                addJavascriptInterface(
                    JavaScriptInterface(context) { showDialog = true },
                    "flipgiveAppInterface"
                )
                loadUrl(url)

                webViewState.value = this
            }
        }, update = {
            it.loadUrl(url)
        })

        if (showDialog) {
            PromptDialog(
                onConfirm = { _, enteredToken ->
                    showDialog = false
                    val javascriptCode = "(() => { if (window && window.updateToken) { window.updateToken(\"$enteredToken\"); } })();"

                    webViewState.value?.let { webView ->
                        webView.evaluateJavascript(javascriptCode, null)
                    }
                },
                onCancel = { showDialog = false }
            )
        }
    }
}

class JavaScriptInterface(private val context: Context, private val requestPromptDialog: () -> Unit) {

    @JavascriptInterface
    fun postMessage(message: String) {
        println("Received message: $message")
        if (message == "USER_DATA_REQUIRED") {
            requestPromptDialog()
        }

        if (message.contains("OPEN_IN_BROWSER::")) {
            val urlPayload = message.replace("OPEN_IN_BROWSER::", "")
            val url = try {
                Uri.parse(urlPayload)
            } catch (e: Exception) {
                null
            }

            url?.let {
                val intent = Intent(Intent.ACTION_VIEW, it)
                context.startActivity(intent)
            }
        }
    }
}
