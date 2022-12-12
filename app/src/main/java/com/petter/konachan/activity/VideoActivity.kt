package com.petter.konachan.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaController2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.petter.konachan.R
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.databinding.ActivityVideoBinding
import com.petter.konachan.response.Image
import com.petter.konachan.util.DisplayUtil
import com.petter.konachan.viewmodel.VideoViewModel

class VideoActivity : BaseActivity<ActivityVideoBinding, VideoViewModel>() {


    companion object {

        fun start(context: Context, url: String) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        val url = intent.getStringExtra("url")
        mActivityBinding.videoView.setVideoPath(url)
        mediaController = MediaController(this)
        mActivityBinding.videoView.setMediaController(mediaController)
        mActivityBinding.videoView.setOnPreparedListener {
            mActivityBinding.progressBar.visibility = View.GONE
        }
        mActivityBinding.videoView.start()
    }

    /**
     * 屏幕旋转事件
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val rotation = DisplayUtil.getDisplay(this).rotation
        Log.i(TAG, "onConfigurationChanged: $newConfig, rot: $rotation")
        val controller = ViewCompat.getWindowInsetsController(mActivityBinding.root)
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            //隐藏状态栏
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }else{
            //显示状态栏
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun isSetLoadSir(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video
    }

    override fun loadSirView(): Any {
        return this
    }

    override fun reload() {

    }

    override fun onBackPressed() {
        if (mediaController.isShowing) {
            mediaController.hide()
        }
        super.onBackPressed()
    }
}