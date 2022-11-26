package com.jjl.mvvm.log

import com.jjl.mvvm.net.interceptor.FormatLogInterceptor

/**
 *
 * @Description:
 * @Author: fanlei
 * @CreateDate: 14/9/2021 上午9:45
 * @Version: 1.0
 */
class NetLogger : FormatLogInterceptor.Logger {

    override fun log(message: String) {
        FLogger.json(message)
    }
}