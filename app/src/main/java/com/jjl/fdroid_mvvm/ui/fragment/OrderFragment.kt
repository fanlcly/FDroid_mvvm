package com.jjl.fdroid_mvvm.ui.fragment

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.jjl.fdroid_mvvm.R
import com.jjl.fdroid_mvvm.databinding.FragmentOrderBinding
import com.jjl.fdroid_mvvm.vm.OrderFViewModel
import com.jjl.mvvm.base.BaseVmFragment

/**
 *
 * Author: fanlei
 * Date: 10/18/2022 10:10 AM
 * Description: OrderFragment
 *
 */
class OrderFragment : BaseVmFragment<FragmentOrderBinding>(FragmentOrderBinding::inflate) {

    private val viewModel by viewModels<OrderFViewModel>()

    override fun initView() {
        setTitle()
    }

    /**
     * 数据观察
     */
    override fun buildObserve() {

    }


    /**
     * 设置标题
     */
    private fun setTitle() {
        binding.llTileLayout.titleBar.apply {
            title = "数据的请求"
            setTitleColor(ContextCompat.getColor(mActivity, R.color.black))
            leftIcon = null
        }
    }


}