package com.jjl.fdroid_mvvm.net.stateCallback

/**
 * @Description: (UpdateUiState)
 * @author fanlei
 * @date  2022/11/21 21:17
 * @version V1.0
 */
data class UpdateUiState<T>(
    //请求是否成功
    var isSuccess: Boolean = true,
    //操作的对象
    var data: T? = null,
    //请求失败的错误信息
    var errorMsg: String = ""
)