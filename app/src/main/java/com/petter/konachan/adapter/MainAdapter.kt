package com.petter.konachan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.petter.konachan.R
import com.petter.konachan.listener.ItemOnClickListener
import com.petter.konachan.network.UrlEnum
import com.petter.konachan.response.Image
import com.petter.konachan.response.ImageEntity
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @anthor: EDZ
 * @time: 2021/10/29 10:29
 * @description:
 */
class MainAdapter(private val context: Context, var data: MutableList<Image>, private val screenWidth: Int) :
    RecyclerView.Adapter<MainAdapter.MainHolder>() {

    var itemOnClickListener : ItemOnClickListener? = null
    var height = 0
    var maxWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return MainHolder(inflate)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val imageResponse = data[position]
        if (height == 0 || maxWidth == 0) {
            maxWidth = screenWidth / 2
            val bigDecimal = BigDecimal(maxWidth).divide(
                BigDecimal(imageResponse.previewWidth),
                4,
                RoundingMode.HALF_UP
            )
            height = BigDecimal(imageResponse.previewHeight).multiply(bigDecimal).toInt()
        }
        Glide.with(context)
            .asBitmap()
            .load(imageResponse.preview)
            .placeholder(R.drawable.ic_banner)
            .apply(RequestOptions.overrideOf(maxWidth, height))
            .thumbnail(if (imageResponse.type == UrlEnum.GELBOORU.code) 0.5F else 1.0F)
            .into(holder.itemView as ImageView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MainHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            itemOnClickListener?.onItemClick(position, v)
        }

    }
}
