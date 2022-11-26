package com.jjl.fdroid_mvvm.net

import com.jjl.mvvm.net.BaseHttpClient
import com.jjl.mvvm.net.NetProvider

object HttpClient : BaseHttpClient() {
    override var provider: NetProvider? = MyNetProvider()

    val service by lazy { getService(ApiService::class.java, ApiService.BASE_URL) }
}
