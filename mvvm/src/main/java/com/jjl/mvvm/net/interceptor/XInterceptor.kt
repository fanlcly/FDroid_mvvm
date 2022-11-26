package com.jjl.mvvm.net.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 *
 * @Description:
 * @Author: fanlei
 * @CreateDate: 14/7/2021 下午3:45
 * @Version: 1.0
 */
class XInterceptor(private val header: RequestHeader) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        request = header.onProceedRequest(request, chain)
        val response = request.let { chain.proceed(it) }
        return header.onProceedResponse(response, chain)
    }
}