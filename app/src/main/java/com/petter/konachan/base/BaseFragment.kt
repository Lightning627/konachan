package com.petter.konachan.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.petter.konachan.base.load.EmptyCallback
import com.petter.konachan.base.load.ErrorCallback
import com.petter.konachan.base.load.LoadCallback

/**
 * @anthor: EDZ
 * @time: 2021/11/9 16:01
 * @description:
 */
abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    lateinit var mFragmentBinding: VB
    lateinit var mViewModel: VM
    lateinit var loadService: LoadService<Any>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return mFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val requireActivity = requireActivity()
        if (requireActivity is BaseActivity<*, *>) {
            this.mViewModel = requireActivity.mViewModel as VM
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
            .build().register(loadSirView(), Callback.OnReloadListener {
                reload()
            })
    }

    abstract fun getLayoutId(): Int
    abstract fun loadSirView(): Any
    abstract fun reload()
    abstract fun isSetLoadSir(): Boolean
}