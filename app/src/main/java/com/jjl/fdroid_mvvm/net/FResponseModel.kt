package com.jjl.fdroid_mvvm.net

import com.jjl.mvvm.net.response.BaseResponse

/**
 * @Description: (自己根据服务端定义response)
 * @author fanlei
 * @date  2022/11/26 15:16
 * @version V1.0
 */
data class FResponseModel<T>(
    private val code: Int? = null,
    val message: String? = null,
    private val data: T? = null
) :
    BaseResponse<T>() {

    override fun isSuccess() = (code == 0 || code == 200)

    override fun getResponseCode() = code ?: 0

    override fun getResponseData() = data

    override fun getResponseMsg() = message ?: ""


}