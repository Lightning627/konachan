package com.petter.konachan.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.petter.konachan.R
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.databinding.ActivityWebBinding
import com.petter.konachan.viewmodel.WebViewModel

class WebActivity : BaseActivity<ActivityWebBinding, WebViewModel>() {

    companion object{

        fun start(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //初始化webview
        mViewModel.initWebView(mActivityBinding.webView)
        //加载URL
        val url = intent.getStringExtra("url")
        url?.let { mActivityBinding.webView.loadUrl(it) }

    }

    override fun isSetLoadSir(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_web
    }

    override fun loadSirView(): Any {
        return this
    }

    override fun reload() {

    }

    override fun onBackPressed() {
        if (mActivityBinding.webView.canGoBack()) {
            mActivityBinding.webView.goBack()
        }
        super.onBackPressed()
    }

}