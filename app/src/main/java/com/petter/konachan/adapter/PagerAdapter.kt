package com.petter.konachan.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView
import com.petter.konachan.R
import com.petter.konachan.base.load.ErrorCallback
import com.petter.konachan.listener.PagerPhotoClickListener
import com.petter.konachan.response.Image

/**
 * @anthor: EDZ
 * @time: 2021/12/2 16:27
 * @description:
 */
class PagerAdapter(private val context: Context, var data: MutableList<Image>) :
    RecyclerView.Adapter<PagerAdapter.Holder>() {

    var pagerPhotoClickListener: PagerPhotoClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pager, parent, false)
        return Holder(inflate)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = data[position]
        loadPhoto(image.image, holder.photoView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun loadPhoto(url: String, view: ImageView) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_banner)
            .into(view)
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view), OnOutsidePhotoTapListener,
        OnPhotoTapListener, View.OnLongClickListener {
        val photoView: PhotoView by lazy {
            view.findViewById(R.id.photoView)
        }

        init {
            photoView.setOnOutsidePhotoTapListener(this)
            photoView.setOnPhotoTapListener(this)
            photoView.setOnLongClickListener(this)
        }

        override fun onOutsidePhotoTap(imageView: ImageView?) {
            pagerPhotoClickListener?.onOutSideTap()
        }

        override fun onPhotoTap(view: ImageView?, x: Float, y: Float) {
            pagerPhotoClickListener?.onPhotoTap()
        }

        override fun onLongClick(v: View?): Boolean {
            pagerPhotoClickListener?.onLongClick(position)
            return true
        }
    }

}
