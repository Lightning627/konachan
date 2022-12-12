package com.petter.konachan.viewmodel

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.petter.konachan.base.BaseViewModel
import com.petter.konachan.base.MyWebViewClient

class WebViewModel: BaseViewModel() {

    fun initWebView(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = MyWebViewClient()

    }
}