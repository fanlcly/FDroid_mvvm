package com.jjl.mvvm.flog

import com.jjl.mvvm.net.interceptor.FormatLogInterceptor

/**
 *
 * @Description:
 * @Author: fanlei
 * @CreateDate: 14/9/2021 上午9:45
 * @Version: 1.0
 */
class NetLogger : FormatLogInterceptor.Logger {

    override fun log(message: String, isShowTopLine: Boolean, isShowBottomLine: Boolean) {
        JsonLog.printJson("net", message, isShowTopLine, isShowBottomLine)
    }
}