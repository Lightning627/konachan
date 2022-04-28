package com.petter.konachan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petter.konachan.R
import com.petter.konachan.constant.GuideEnum
import com.petter.konachan.listener.ItemOnClickListener

/**
 * @author EDZ
 * @time 2022/4/27 16:00
 */
class GuideAdapter(val context: Context, val onClickListener: ItemOnClickListener): RecyclerView.Adapter<GuideAdapter.GuideHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_guide, parent, false)
        return GuideHolder(inflate)
    }

    override fun onBindViewHolder(holder: GuideHolder, position: Int) {
        val guideEnum = GuideEnum.values()[position]
        Glide.with(context)
            .load(guideEnum.holder)
            .centerCrop()
            .into(holder.ivHead)
        holder.tvName.text = guideEnum.desc
    }

    override fun getItemCount(): Int {
        return GuideEnum.values().size
    }

    inner class GuideHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val ivHead: ImageView by lazy {
            view.findViewById(R.id.ivHead)
        }
        val tvName: TextView by lazy {
            view.findViewById(R.id.tvName)
        }
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            onClickListener.onItemClick(adapterPosition, p0)
        }
    }

}
