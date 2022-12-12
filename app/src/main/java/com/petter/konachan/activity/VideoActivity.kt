package com.petter.konachan.activity

import android.content.Context
import android.content.Intent
import android.media.MediaController2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import com.petter.konachan.R
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.databinding.ActivityVideoBinding
import com.petter.konachan.response.Image
import com.petter.konachan.viewmodel.VideoViewModel

class VideoActivity : BaseActivity<ActivityVideoBinding, VideoViewModel>() {


    companion object {

        fun start(context: Context, url: String) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        val url = intent.getStringExtra("url")
        mActivityBinding.videoView.setVideoPath(url)
        val mediaController = MediaController(this)
        mActivityBinding.videoView.setMediaController(mediaController)
        mActivityBinding.videoView.setOnPreparedListener {
            mActivityBinding.progressBar.visibility = View.GONE
        }
        mActivityBinding.videoView.start()
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
}