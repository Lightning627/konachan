package com.petter.konachan.base

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.petter.konachan.constant.Constant
import com.petter.konachan.util.SPUtil

class MyWebViewClient: WebViewClient() {

    private val TAG = "MyWebViewClient"

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        view?.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        val cookieManager = CookieManager.getInstance()
        val cookie = cookieManager.getCookie(url)
        Log.i(TAG, "onPageFinished: $cookie")
        SPUtil.saveString(Constant.SP_KEY_KONACHAN_COOKIE, cookie)
        super.onPageFinished(view, url)
    }
}