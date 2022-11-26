package com.jjl.fdroid_mvvm.net

import com.jjl.mvvm.BuildConfig
import com.jjl.mvvm.net.NetProvider
import com.jjl.mvvm.net.interceptor.RequestHeader
import okhttp3.Interceptor

class MyNetProvider : NetProvider {
    override fun configConnectTimeoutMills(): Long {
        return 15
    }

    override fun configReadTimeoutMills(): Long {
        return 15
    }

    override fun configInterceptors(): Array<Interceptor>? {
        return null
    }

    override fun configLogEnable(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun configHeader(): RequestHeader? {
        return null
    }
}