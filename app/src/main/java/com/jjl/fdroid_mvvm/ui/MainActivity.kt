package com.jjl.fdroid_mvvm.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jjl.fdroid_mvvm.R
import com.jjl.fdroid_mvvm.databinding.ActivityMainBinding
import com.jjl.mvvm.base.BaseVmActivity
import com.jjl.mvvm.ext.launchRequest

class MainActivity :
    BaseVmActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

//    private val viewModel by viewModels<MainViewModel>()

    override fun initView() {
        val navController: NavController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)
        binding.navView.setOnItemSelectedListener { item ->
            var bundle = Bundle()
            navController.navigate(item.itemId, bundle)
            true
        }

    }



}

