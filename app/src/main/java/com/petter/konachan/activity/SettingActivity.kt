package com.petter.konachan.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import com.petter.konachan.R
import com.petter.konachan.base.BaseActivity
import com.petter.konachan.base.MyApplication
import com.petter.konachan.databinding.ActivitySettingBinding
import com.petter.konachan.network.UrlEnum
import com.petter.konachan.util.SPUtil
import com.petter.konachan.viewmodel.SettingViewModel

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>(),
    View.OnClickListener {

    private val checkBoxes = mutableListOf<RadioButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    private fun initView() {
        mActivityBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        checkBoxes.add(mActivityBinding.rbHx)
        checkBoxes.add(mActivityBinding.rbCr)
        checkBoxes.add(mActivityBinding.rbGel)
        when (SPUtil.getInt("api")) {
            UrlEnum.KONACHAN_MAN.code -> {
                check(mActivityBinding.rbCr)
            }
            UrlEnum.GELBOORU.code -> {
                check(mActivityBinding.rbGel)
            }
            else -> {
                check(mActivityBinding.rbHx)
            }
        }
        mActivityBinding.conHx.setOnClickListener(this)
        mActivityBinding.conCr.setOnClickListener(this)
        mActivityBinding.conGel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.conHx -> {
                check(mActivityBinding.rbHx)
                setApi(UrlEnum.KONACHAN_CHILDREN.code)
            }
            R.id.conCr -> {
                check(mActivityBinding.rbCr)
                setApi(UrlEnum.KONACHAN_MAN.code)
            }
            R.id.conGel -> {
                check(mActivityBinding.rbGel)
                setApi(UrlEnum.GELBOORU.code)
            }
        }
        setResult(Activity.RESULT_OK)
    }

    override fun loadSirView(): Any {
        return this
    }

    override fun reload() {

    }

    override fun isSetLoadSir(): Boolean {
        return false
    }

    private fun check(view: RadioButton) {
        for (checkBox in checkBoxes) {
            checkBox.isChecked = checkBox == view
        }
    }

    private fun setApi(code: Int) {
        SPUtil.saveInt("api", code)
        MyApplication.setApiType(code)
    }

}