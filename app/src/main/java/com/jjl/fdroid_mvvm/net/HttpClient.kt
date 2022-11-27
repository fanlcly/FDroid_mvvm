package com.jjl.fdroid_mvvm.net

import com.jjl.mvvm.net.BaseHttpClient
import com.jjl.mvvm.net.NetProvider

object HttpClient : BaseHttpClient() {
    // 如果不需要配置可以不用重写
    override var provider: NetProvider? = MyNetProvider()

    val service by lazy { getService(ApiService::class.java, ApiService.BASE_URL) }
}
