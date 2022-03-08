package com.petter.konachan.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.petter.konachan.R
import com.petter.konachan.adapter.MainAdapter
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.listener.ItemOnClickListener
import com.petter.konachan.base.load.EmptyCallback
import com.petter.konachan.base.load.LoadCallback
import com.petter.konachan.databinding.ActivityFavoriteBinding
import com.petter.konachan.db.AppDatabase
import com.petter.konachan.db.ImageDao
import com.petter.konachan.response.Image
import com.petter.konachan.util.ScreenUtil
import com.petter.konachan.viewmodel.MainViewModel

class FavoriteActivity : BaseActivity<ActivityFavoriteBinding, MainViewModel>(),
    View.OnClickListener, ItemOnClickListener {

    private lateinit var imageDao: ImageDao
    private lateinit var adapter: MainAdapter
    private lateinit var tags: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDao = AppDatabase.getInstance(this).imageDao()
        initView()
        loadService.showCallback(EmptyCallback::class.java)
        search()
    }

    private fun initView() {
        mActivityBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
        mActivityBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = MainAdapter(this, mutableListOf(), ScreenUtil.getScreenWidth(this))
        adapter.itemOnClickListener = this
        mActivityBinding.recyclerView.adapter = adapter
        mActivityBinding.btSearch.setOnClickListener(this)
        mActivityBinding.btStartSearch.setOnClickListener(this)
        mActivityBinding.btTop.setOnClickListener(this)
        mActivityBinding.etTag.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
            }
            false
        }
        mActivityBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var scrolled = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrolled += dy
                if (scrolled > 300) {
                    mActivityBinding.btTop.show()
                } else {
                    mActivityBinding.btTop.hide()
                }
            }
        })
    }

    private fun search() {
        adapter.data.clear()
        tags = mActivityBinding.etTag.text.toString()
        loadService.showCallback(LoadCallback::class.java)
        val postByDb = mViewModel.postByDb(imageDao, mActivityBinding.etTag.text.toString())
        adapter.data.addAll(postByDb)
        if (adapter.itemCount == 0) {
            loadService.showCallback(EmptyCallback::class.java)
        } else {
            loadService.showSuccess()
        }
        adapter.notifyDataSetChanged()
        mActivityBinding.drawLayout.closeDrawer(GravityCompat.START)
        if (currentFocus != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                currentFocus?.windowToken,
                0
            )
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btSearch -> {
                mActivityBinding.drawLayout.openDrawer(GravityCompat.START)
            }
            R.id.btStartSearch -> {
                search()
            }
            R.id.btTop -> {
                mActivityBinding.recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    override fun isSetLoadSir(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_favorite
    }

    override fun loadSirView(): Any {
        return mActivityBinding.recyclerView
    }

    override fun reload() {

    }

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val remove = it.data?.getSerializableExtra("remove") as ArrayList<Int>
            val iterator = adapter.data.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (remove.contains(next.id)) {
                    iterator.remove()
                }
            }
            adapter.notifyDataSetChanged()
            if (adapter.data.size == 0) {
                loadService.showCallback(EmptyCallback::class.java)
            }else{
                var position = it.data?.getIntExtra("position", 0)
                if (position == null) position = 0
                if (position > adapter.data.size - 1) position = adapter.data.size - 1
                mActivityBinding.recyclerView.scrollToPosition(position)
            }
        }

    override fun onItemClick(position: Int, view: View) {
//        clickIndex = position
//        val intent = Intent(this, PhotoViewActivity::class.java)
//        intent.putExtra("data", adapter.data[position])
//        registerForActivityResult.launch(intent)
//        startActivity(
//            intent,
//            ActivityOptions.makeSceneTransitionAnimation(this, view, "share").toBundle()
//        )
        val intent = Intent(this, PagerActivity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("collection", true)
        intent.putExtra("data", ArrayList(adapter.data))
        registerForActivityResult.launch(intent)

    }
}