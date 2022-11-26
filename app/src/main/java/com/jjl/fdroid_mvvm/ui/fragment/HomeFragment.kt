package com.jjl.fdroid_mvvm.ui.fragment

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjl.fdroid_mvvm.R
import com.jjl.fdroid_mvvm.adapter.HomeAdapter
import com.jjl.fdroid_mvvm.databinding.FragmentHomeBinding
import com.jjl.fdroid_mvvm.vm.HomeFViewModel
import com.jjl.mvvm.base.BaseVmFragment
import com.jjl.mvvm.ext.launchRequest


/**
 *
 * Author: fanlei
 * Date: 10/18/2022 10:10 AM
 * Description: HomeFragment
 *
 */
class HomeFragment : BaseVmFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var homeAdapter: HomeAdapter
    private val viewModel by viewModels<HomeFViewModel>()

    override fun initView() {
        setTitle()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(mActivity)
            homeAdapter = HomeAdapter(viewModel.dataState.value?.listData)
            adapter = homeAdapter
        }
    }

    /**
     * 数据观察
     */
    override fun buildObserve() {
        viewModel.run {
            dataState.observe(this@HomeFragment) {
                if (it.isSuccess) {
                    homeAdapter.setList(it.listData)
                } else {
                    Toast.makeText(mActivity, it.errMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        requestList()
    }

    /**
     * 发起请求
     */
    private fun requestList() = launchRequest(viewModel::getHotData)


    /**
     * 设置标题
     */
    private fun setTitle() {
        binding.llTileLayout.titleBar.apply {
            title = "列表数据的请求"
            setTitleColor(ContextCompat.getColor(mActivity, R.color.black))
            leftIcon = null
        }
    }


}