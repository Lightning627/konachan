package com.petter.konachan.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.petter.konachan.R
import com.petter.konachan.adapter.DropListAdapter
import com.petter.konachan.adapter.MainAdapter
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.base.GlobalData
import com.petter.konachan.listener.ItemOnClickListener
import com.petter.konachan.base.load.EmptyCallback
import com.petter.konachan.base.load.ErrorCallback
import com.petter.konachan.base.load.LoadCallback
import com.petter.konachan.databinding.ActivityMainBinding
import com.petter.konachan.response.BaseResponse
import com.petter.konachan.response.Image
import com.petter.konachan.util.ScreenUtil
import com.petter.konachan.viewmodel.MainViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), OnRefreshLoadMoreListener,
    ItemOnClickListener, View.OnClickListener, TextWatcher {

    private lateinit var adapter: MainAdapter
    private var page = 1
    private var tags: String? = null
    private lateinit var tagAdapter: DropListAdapter
    private var refresh = false
    private var loadmore = false

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                page = 1
                tags = null
                mActivityBinding.refreshLayout.autoRefresh()
            }
        }
    private val pagerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            page = it.data?.getIntExtra("page", 1)!!
            adapter.notifyDataSetChanged()
            mActivityBinding.recyclerView.scrollToPosition(it.data?.getIntExtra("position", 0)!!)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.imageLiveData.observe(this, posted)
        mViewModel.tagsLiveData.observe(this, tagsPost)
        initView()
        mActivityBinding.refreshLayout.autoRefresh(1000)
    }

    private fun initView() {
        tagAdapter = DropListAdapter(this, mutableListOf())
        mActivityBinding.etTag.setAdapter(tagAdapter)
        mActivityBinding.etTag.addTextChangedListener(this)
        mActivityBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = MainAdapter(this, GlobalData.images, ScreenUtil.getScreenWidth(this))
        adapter.itemOnClickListener = this
        mActivityBinding.recyclerView.adapter = adapter
        mActivityBinding.refreshLayout.setRefreshHeader(MaterialHeader(this))
        mActivityBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this))
        mActivityBinding.refreshLayout.setOnRefreshLoadMoreListener(this)
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
        loadService.showSuccess()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (!checkWifiStatus()) {
            Toast.makeText(this, "由于服务器限制，\n请先连接wifi食用该app", Toast.LENGTH_SHORT).show()
            refreshLayout.finishRefresh()
            return
        }
        refresh = true
        page = 1
        mViewModel.post(page, 14, tags)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!checkWifiStatus()) {
            Toast.makeText(this, "由于服务器限制，\n请先连接wifi食用该app", Toast.LENGTH_SHORT).show()
            refreshLayout.finishLoadMore()
            return
        }
        loadmore = true
        page++
        mViewModel.post(page, 14, tags)
    }

    private fun checkWifiStatus(): Boolean {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            return false
        }
        return when (wifiManager.wifiState) {
            WifiManager.WIFI_STATE_ENABLED -> {
                val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetwork
                activeNetwork != null
            }
            else -> false
        }
    }

    private val tagsPost: Observer<MutableList<String>> = Observer {
        tagAdapter.data.clear()
        tagAdapter.data.addAll(it)
        tagAdapter.notifyDataSetChanged()
        mActivityBinding.etTag.doBeforeTextChanged { _, _, _, _ -> }
        mActivityBinding.etTag.doAfterTextChanged { }
    }

    private val posted: Observer<BaseResponse<MutableList<Image>>> = Observer {
        if (it.success) {
            if (it.data?.size == 0 && refresh) {
                loadService.showCallback(EmptyCallback::class.java)
                return@Observer
            }
            loadService.showSuccess()
            Log.i(
                TAG,
                "刷新状态: isRefreshing: $refresh, 加载状态isLoadMore: $loadmore"
            )
            if (refresh) adapter.data.clear()
            val size = adapter.data.size
            adapter.data.addAll(it.data!!)
            if (refresh) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemRangeInserted(size, it.data?.size!!)
            }
            if (mActivityBinding.refreshLayout.isLoading && it.data?.size == 0) {
                mActivityBinding.refreshLayout.setNoMoreData(true)
            }
            mActivityBinding.refreshLayout.finishLoadMore(true)
            mActivityBinding.refreshLayout.finishRefresh(true)
        } else {
            mActivityBinding.refreshLayout.finishLoadMore(false)
            mActivityBinding.refreshLayout.finishRefresh(false)
            Toast.makeText(this, it.reason, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Observer: ${it.reason}")
            loadService.showCallback(ErrorCallback::class.java)
        }
        refresh = false
        loadmore = false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> registerForActivityResult.launch(
                Intent(
                    this,
                    SettingActivity::class.java
                )
            )
            R.id.favorite -> startActivity(Intent(this, FavoriteActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(position: Int, view: View) {
        //带动画的跳转
//        val intent = Intent(this, PhotoViewActivity::class.java)
//        intent.putExtra("data", adapter.data[position])
//        startActivity(
//            intent,
//            ActivityOptions.makeSceneTransitionAnimation(this, view, "share").toBundle()
//        )
        //PagerActivity
        val intent = Intent(this, PagerActivity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("page", page)
        intent.putExtra("tags", tags)
        pagerForActivityResult.launch(intent)
    }

    private var exit = false

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            exit = false
        }
    }

    override fun onBackPressed() {
        if (exit) {
            super.onBackPressed()
        } else {
            exit = true
            handler.sendEmptyMessageDelayed(0, 1000)
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
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

    override fun loadSirView(): Any {
        return mActivityBinding.refreshLayout
    }

    override fun reload() {
        if (!checkWifiStatus()) {
            Toast.makeText(this, "由于服务器限制，\n请先连接wifi食用该app", Toast.LENGTH_SHORT).show()
            return
        }
        loadService.showCallback(LoadCallback::class.java)
        refresh = true
        mViewModel.post(page, 14, tags)
    }

    private fun search() {
        page = mViewModel.getPage(mActivityBinding.etPage.text.toString())
        tags = mActivityBinding.etTag.text.toString()
        loadService.showCallback(LoadCallback::class.java)
        refresh = true
        mViewModel.post(page, 14, tags)
        mActivityBinding.drawLayout.closeDrawer(GravityCompat.START)
        mActivityBinding.refreshLayout.autoRefreshAnimationOnly()
        if (currentFocus != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                currentFocus?.windowToken,
                0
            )
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            if (it.length > 2) {
                mViewModel.queryTag(it.toString())
            }
        }
    }

}