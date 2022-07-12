package com.petter.konachan.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.petter.konachan.R
import com.petter.konachan.adapter.GuideAdapter
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.constant.GuideEnum
import com.petter.konachan.databinding.ActivityGuideBinding
import com.petter.konachan.listener.ItemOnClickListener
import com.petter.konachan.viewmodel.GuideViewModel

class GuideActivity : BaseActivity<ActivityGuideBinding, GuideViewModel>(), ItemOnClickListener {

    private var exit = false
    private lateinit var searchView: SearchView

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            exit = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        mActivityBinding.recyclerView.adapter = GuideAdapter(this, this)
    }

    override fun onItemClick(position: Int, view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("tag", GuideEnum.values()[position].tag)
        startActivity(intent)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val findItem = menu.findItem(R.id.searchView)
        searchView = findItem.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(this@GuideActivity, MainActivity::class.java)
                intent.putExtra("tag", searchView.query.toString())
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> startActivity(Intent(this, SettingActivity::class.java))
            R.id.favorite -> startActivity(Intent(this, FavoriteActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_guide
    }

    override fun isSetLoadSir(): Boolean {
        return false
    }

    override fun loadSirView(): Any {
        return mActivityBinding.recyclerView
    }

    override fun reload() {

    }

}