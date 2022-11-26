package com.jjl.mvvm.ext

import androidx.lifecycle.lifecycleScope
import com.jjl.mvvm.base.IView
import com.jjl.mvvm.net.response.BaseResponse
import com.jjl.mvvm.net.response.ResponseState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @Description: ()
 * @author fanlei
 * @date  2022/11/22 20:23
 * @version V1.0
 */

/**
 * flow封装loading框
 */
fun IView.launchRequest(requestBlock: suspend () -> Unit, isLoading: Boolean = true) {
    lifecycleScope.launch {
        flow {
            emit(requestBlock())
        }.onStart {
            if (isLoading) {
                showLoading()
            }
        }.onCompletion {
            dismissLoading()
        }.collect()

    }
}



