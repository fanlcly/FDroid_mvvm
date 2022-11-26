package com.jjl.mvvm.net.response

import androidx.lifecycle.MutableLiveData
import com.jjl.mvvm.net.error.AppException
import com.jjl.mvvm.net.error.ExceptionResult

/**
 * @Description: (ResponseState)
 * @author fanlei
 * @date  2022/11/26 14:13
 * @version V1.0
 */

sealed class ResponseState<out T> {
    companion object {
        fun <T> onAppSuccess(data: T?): ResponseState<T> = Success(data)
        fun <T> onAppError(error: AppException): ResponseState<T> = Error(error)
    }
}

data class Success<out T>(val data: T?) : ResponseState<T>()
data class Error(val error: AppException) : ResponseState<Nothing>()


/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableLiveData<ResponseState<T>>.paresResult(result: BaseResponse<T>) {
    value = when {
        result.isSuccess() -> {
            ResponseState.onAppSuccess(result.getResponseData())
        }
        else -> {
            ResponseState.onAppError(
                AppException(
                    result.getResponseCode(),
                    result.getResponseMsg()
                )
            )
        }
    }
}

/**
 * 异常转换异常处理
 */
fun <T> MutableLiveData<ResponseState<T>>.paresException(e: Throwable) {
    this.value = ResponseState.onAppError(ExceptionResult.handleException(e))
}




