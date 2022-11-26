package com.jjl.mvvm.base

import androidx.lifecycle.ViewModel
import com.jjl.mvvm.net.error.AppException
import com.jjl.mvvm.net.error.ExceptionResult
import com.jjl.mvvm.net.response.BaseResponse

/**
 * @Description: (省略了仓库层的BaseViewModel，实际开发中，基本都是单数据源,BaseViewModel封装了常规的请求方法,其它的方法以扩展函数实现)
 * @author fanlei
 * @date  2022/11/18 20:44
 * @version V1.0
 */
open class BaseViewModel : ViewModel() {

    /**
     * 处理请求信息
     */
    suspend fun <T> handleResponse(
        block: suspend () -> BaseResponse<T>,
        success: (T?) -> Unit,
        error: (AppException) -> Unit = {},
    ) {
        runCatching {
            block.invoke()
        }.onSuccess { response ->
            executeResult(response, success, error)
        }.onFailure { e ->
            //打印错误栈信息
            e.printStackTrace()
            //失败回调
            error(ExceptionResult.handleException(e))
        }
    }


    /**
     * 处理返回结果
     */
    private fun <T> executeResult(
        response: BaseResponse<T>,
        success: (T?) -> Unit,
        error: (AppException) -> Unit
    ) {
        runCatching {
            when {
                response.isSuccess() -> {
                    success(response.getResponseData())
                }
                else -> {
                    error(
                        AppException(
                            response.getResponseCode(),
                            response.getResponseMsg(),
                        )
                    )
                }
            }
        }.onFailure { e ->
            //打印错误栈信息
            e.printStackTrace()
            //失败回调
            error(ExceptionResult.handleException(e))
        }
    }
}


