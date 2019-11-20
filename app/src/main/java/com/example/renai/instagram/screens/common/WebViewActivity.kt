package com.example.renai.instagram.screens.common

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebViewClient
import com.example.renai.instagram.R
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView.webViewClient = WebViewClient()
        val url = intent.getStringExtra("url")
        webView.loadUrl(url)
        Log.d("WebViewActivity", "onCreate: $url")
    }
}
