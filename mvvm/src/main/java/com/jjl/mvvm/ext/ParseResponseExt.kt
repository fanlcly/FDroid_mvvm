package com.jjl.mvvm.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.jjl.mvvm.base.BaseViewModel
import com.jjl.mvvm.net.error.AppException
import com.jjl.mvvm.net.response.*

/**
 * @Description: (parseResponseExt)
 * @author fanlei
 * @date  2022/11/26 14:07
 * @version V1.0
 */


/**
 * 处理返回数据
 */
suspend fun <T> BaseViewModel.handleParseResponse(
    block: suspend () -> BaseResponse<T>,
    resultState: MutableLiveData<ResponseState<T>>
) {
    runCatching {
        //请求体
        block()
    }.onSuccess {
        resultState.paresResult(it)
    }.onFailure {
        //打印错误栈信息
        it.printStackTrace()
        resultState.paresException(it)
    }
}

/**
 * activity扩展方法，处理成功/失败的状态
 *
 */
fun <T> AppCompatActivity.parseResponseResult(
    resultState: ResponseState<T?>,
    onSuccess: (T?) -> Unit,
    onError: ((AppException) -> Unit)? = null
) {
    when (resultState) {
        is Success -> {
            onSuccess(resultState.data)
        }
        is Error -> {
            onError?.run { resultState.error }
        }
    }
}

/**
 * Fragment，处理成功/失败的状态
 *
 */
fun <T> Fragment.parseResponseResult(
    resultState: ResponseState<T?>,
    onSuccess: (T?) -> Unit,
    onError: ((AppException) -> Unit)? = null
) {
    when (resultState) {
        is Success -> {
            onSuccess(resultState.data)
        }
        is Error -> {
            onError?.run { resultState.error }
        }
    }
}



