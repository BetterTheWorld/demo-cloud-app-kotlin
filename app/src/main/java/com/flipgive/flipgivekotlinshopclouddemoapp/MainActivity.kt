package com.flipgive.flipgivekotlinshopclouddemoapp

import PromptDialog
import WebViewComponent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*


class MainActivity : ComponentActivity() {

    private var baseUrl: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var showPromptDialog by remember { mutableStateOf(true) }
            var baseUrl by remember { mutableStateOf("") }
            var token by remember { mutableStateOf("") }

            // Enable WebView debugging
            WebView.setWebContentsDebuggingEnabled(true)

            if (showPromptDialog) {
                PromptDialog(
                    onConfirm = { providedBaseUrl: String, providedToken: String ->
                        baseUrl = providedBaseUrl
                        token = providedToken
                        showPromptDialog = false
                    },
                    onCancel = { finish() }
                )
            } else {
                WebViewComponent(baseUrl, token)
            }
        }
    }
}