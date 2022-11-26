package com.jjl.fdroid_mvvm.ui.fragment

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.jjl.fdroid_mvvm.R
import com.jjl.fdroid_mvvm.databinding.FragmentMineBinding
import com.jjl.fdroid_mvvm.vm.MineFViewModel
import com.jjl.mvvm.base.BaseVmFragment
import com.jjl.mvvm.ext.launchRequest
import com.jjl.mvvm.ext.parseResponseResult

/**
 *
 * Author: fanlei
 * Date: 10/18/2022 10:14 AM
 * Description: 我的
 *
 */
class MineFragment : BaseVmFragment<FragmentMineBinding>(FragmentMineBinding::inflate) {

    private val viewModel by viewModels<MineFViewModel>()

    override fun initView() {
        setTitle()
        binding.apply {
            tvRefresh.setOnClickListener {
                requestList()
            }
        }

    }


    override fun buildObserve() {
        viewModel.genreResult.observe(this) { it ->
            parseResponseResult(it, {
                if (it?.isNotEmpty() == true) {
                    binding.tvResult.text = it.get(0).genre
                }
            }) {
                Toast.makeText(mActivity, it.errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
        requestList()
    }

    /**
     * 发起请求
     */
    private fun requestList() = launchRequest(viewModel::queryCompositionGenre)


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