package com.petter.konachan.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.ProgressCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.petter.konachan.R
import com.petter.konachan.base.load.EmptyCallback
import com.petter.konachan.base.load.ErrorCallback
import com.petter.konachan.base.load.LoadCallback
import java.lang.reflect.ParameterizedType

/**
 * @anthor: EDZ
 * @time: 2021/10/28 16:38
 * @description:
 */
abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    val TAG: String = this::class.java.simpleName

    lateinit var mActivityBinding: VB
    lateinit var mViewModel: VM
    lateinit var loadService: LoadService<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化viewBinding
        mActivityBinding = DataBindingUtil.setContentView(this, getLayoutId())
        //Toolbar
        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        toolbar?.let { setSupportActionBar(it) }

        val superclass = javaClass.genericSuperclass
        if (superclass != null) {
            val actualTypeArguments =
                (superclass as ParameterizedType).actualTypeArguments
            // 统一处理初始化ViewModel
            val viewModelClass = actualTypeArguments[1] as Class<VM>
            mViewModel =
                ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                    viewModelClass
                )
            mViewModel.mLifecycleOwner = this
        }
        if (isSetLoadSir()) {
            initLoadSir()
        }
    }

    private fun initLoadSir() {
        loadService = LoadSir.Builder()
            .addCallback(EmptyCallback())
            .addCallback(ErrorCallback())
            .addCallback(LoadCallback())
            .build().register(loadSirView()) {
                reload()
            }
    }

    abstract fun isSetLoadSir(): Boolean

    abstract fun getLayoutId(): Int

    abstract fun loadSirView(): Any

    abstract fun reload()
}