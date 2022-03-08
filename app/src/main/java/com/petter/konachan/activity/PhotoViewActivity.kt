package com.petter.konachan.activity

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.petter.konachan.R
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.base.load.ErrorCallback
import com.petter.konachan.base.load.LoadCallback
import com.petter.konachan.databinding.ActivityPhotoViewBinding
import com.petter.konachan.listener.FavoriteChangeListener
import com.petter.konachan.response.Image
import com.petter.konachan.response.ImageEntity
import com.petter.konachan.viewmodel.MainViewModel
import com.petter.konachan.widget.PhotoDetailBottomMenu

class PhotoViewActivity : BaseActivity<ActivityPhotoViewBinding, MainViewModel>(),
    OnOutsidePhotoTapListener, OnPhotoTapListener, View.OnLongClickListener {

    lateinit var imageEntity: Image
    lateinit var photoDetailBottomMenu: PhotoDetailBottomMenu
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageEntity = intent.getSerializableExtra("data") as Image
        loadPhoto()
        mActivityBinding.photoView.setOnOutsidePhotoTapListener(this)
        mActivityBinding.photoView.setOnPhotoTapListener(this)
        mActivityBinding.photoView.setOnLongClickListener(this)
        loadService.showCallback(LoadCallback::class.java)
    }

    private fun loadPhoto() {
        Glide.with(this)
            .load(imageEntity.image)
            .thumbnail(
                Glide.with(this)
//                    .load(byteArrayOf())
                    .load(imageEntity.preview)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(this@PhotoViewActivity, e?.message, Toast.LENGTH_LONG).show()
//                    mActivityBinding.progressBar.visibility = View.GONE
                    loadService.showCallback(ErrorCallback::class.java)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
//                    mActivityBinding.progressBar.visibility = View.GONE
                    loadService.showSuccess()
                    mActivityBinding.photoView.setImageDrawable(resource)
                    return false
                }
            })
            .into(mActivityBinding.photoView)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_photo_view
    }

    override fun onOutsidePhotoTap(imageView: ImageView?) {
        onBackPressed()
    }

    override fun onPhotoTap(view: ImageView?, x: Float, y: Float) {
        onBackPressed()
    }

    override fun onLongClick(v: View): Boolean {
        if (!::photoDetailBottomMenu.isInitialized) {
            photoDetailBottomMenu = PhotoDetailBottomMenu(this, imageEntity)
//            photoDetailBottomMenu.favoriteChangeListener = object: FavoriteChangeListener{
//                override fun onChanged(isAdd: Boolean) {
//                    isFavorite = isAdd
//                }
//            }
        }
        photoDetailBottomMenu.show()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isFavorite) {
            setResult(Activity.RESULT_OK)
        }
    }

    override fun loadSirView(): Any {
        return mActivityBinding.photoView
    }

    override fun reload() {
        loadService.showCallback(LoadCallback::class.java)
        loadPhoto()
    }

    override fun isSetLoadSir(): Boolean {
        return true
    }
}