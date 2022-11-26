package com.jjl.fdroid_mvvm.net

import com.jjl.mvvm.net.response.BaseResponse

/**
 * @Description: (继承 BaseResponse )
 *
 * 由于两个接口来自不同的平台，所以用了两个responseModel,正常情况在单服务器环境下，只需要一个model就行了。
 *
 * @author fanlei
 * @date  2022/11/21 21:09
 * @version V1.0
 */
data class ResponseModel<T>(
    val errorCode: Int? = null,
    val errorMsg: String? = null,
    val data: T? = null
) :
    BaseResponse<T>() {

    //  服务端返回的code为0 就代表请求成功，请你根据自己的业务需求来改变
    override fun isSuccess() = errorCode == 0

    override fun getResponseCode() = errorCode ?: 0

    override fun getResponseData() = data

    override fun getResponseMsg() = errorMsg ?: ""


}