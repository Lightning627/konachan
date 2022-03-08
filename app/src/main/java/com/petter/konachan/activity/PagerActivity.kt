package com.petter.konachan.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.petter.konachan.R
import com.petter.konachan.adapter.PagerAdapter
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.base.GlobalData
import com.petter.konachan.databinding.ActivityPagerBinding
import com.petter.konachan.listener.FavoriteChangeListener
import com.petter.konachan.listener.PagerPhotoClickListener
import com.petter.konachan.response.BaseResponse
import com.petter.konachan.response.Image
import com.petter.konachan.viewmodel.MainViewModel
import com.petter.konachan.widget.PhotoDetailBottomMenu

class PagerActivity : BaseActivity<ActivityPagerBinding, MainViewModel>(), PagerPhotoClickListener {

    private lateinit var photoDetailBottomMenu: PhotoDetailBottomMenu
    private val mIntent: Intent by lazy { Intent() }
    private val removedCollection = ArrayList<Int>()
    private var page = 1
    private var tags: String? = null
    private var isCollectionMode = false
    private lateinit var pagerAdapter : PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        page = intent.getIntExtra("page", 1)
        tags = intent.getStringExtra("tags")
        isCollectionMode = intent.getBooleanExtra("collection", false)
        if (isCollectionMode) {
            val data = intent.getSerializableExtra("data") as ArrayList<Image>
            val data1 = mutableListOf<Image>()
            data1.addAll(data)
            pagerAdapter = PagerAdapter(this, data1)
        }else{
            pagerAdapter = PagerAdapter(this, GlobalData.images)
        }

        pagerAdapter.pagerPhotoClickListener = this
        mActivityBinding.pager.adapter = pagerAdapter
        mActivityBinding.pager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (!isCollectionMode) {
                    if (position == GlobalData.images.size - 1) {
                        page++
                        mViewModel.post(page, 14, tags)
                        Log.i(TAG, "onPageSelected: 触发检索")
                    }
                }
            }
        })
        mActivityBinding.pager.setCurrentItem(intent.getIntExtra("position", 0), false)
        mViewModel.imageLiveData.observe(this, posted)
    }

    private val posted: Observer<BaseResponse<MutableList<Image>>> = Observer {
        if (it.success) {
            val size = GlobalData.images.size
            pagerAdapter.data.addAll(it.data!!)
            pagerAdapter.notifyItemRangeInserted(size, it.data?.size!!)
        } else {
            Toast.makeText(this, it.reason, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Observer: ${it.reason}")
        }
    }

    override fun isSetLoadSir(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pager
    }

    override fun loadSirView(): Any {
        return this
    }

    override fun reload() {

    }

    private fun loadMore() {

    }

    override fun onLongClick(position: Int) {
        if (!::photoDetailBottomMenu.isInitialized) {
            photoDetailBottomMenu = PhotoDetailBottomMenu(this, pagerAdapter.data[position])
            photoDetailBottomMenu.favoriteChangeListener = object : FavoriteChangeListener {
                override fun onChanged(imageId: Int, isAdd: Boolean) {
                    if (isAdd) {
                        removedCollection.remove(imageId)
                    } else {
                        removedCollection.add(imageId)
                    }
                }
            }
        }
        photoDetailBottomMenu.setData(pagerAdapter.data[position])
        photoDetailBottomMenu.show()
    }

    override fun onBackPressed() {
        mIntent.putExtra("remove", removedCollection)
        mIntent.putExtra("position", mActivityBinding.pager.currentItem)
        mIntent.putExtra("page", page)
        setResult(RESULT_OK, mIntent)
        super.onBackPressed()
    }

    override fun onPhotoTap() {
        onBackPressed()
    }

    override fun onOutSideTap() {
        onBackPressed()
    }
}