package com.jjl.mvvm.net

import com.jjl.mvvm.BuildConfig
import com.jjl.mvvm.flog.NetLogger
import com.jjl.mvvm.net.interceptor.FormatLogInterceptor
import com.jjl.mvvm.net.interceptor.RequestHeader
import com.jjl.mvvm.net.interceptor.XInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Description: (BaseHttpClient)
 * @author fanlei
 * @date  2022/11/19 19:53
 * @version V1.0
 */
open class BaseHttpClient {

    private val connectTimeoutMills: Long = 10
    private val readTimeoutMills: Long = 10
    protected open var provider: NetProvider? = null

    private fun getClient(): OkHttpClient {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.connectTimeout(
            if (provider?.configConnectTimeoutMills() != 0L) provider?.configConnectTimeoutMills()
                ?: connectTimeoutMills else connectTimeoutMills,
            TimeUnit.SECONDS
        )
        builder.readTimeout(
            if (provider?.configReadTimeoutMills() != 0L) provider?.configReadTimeoutMills()
                ?: connectTimeoutMills else readTimeoutMills,
            TimeUnit.SECONDS
        )
        builder.retryOnConnectionFailure(true)
        val header: RequestHeader? = provider?.configHeader()
        header?.let {
            builder.addNetworkInterceptor(XInterceptor(it))
        }
        val interceptors: Array<Interceptor>? = provider?.configInterceptors()
        interceptors?.forEach {
            builder.addInterceptor(it)
        }

        provider?.configLogEnable()?.let {
            val netLogger = FormatLogInterceptor(NetLogger())
            netLogger.setLevel(if (it) FormatLogInterceptor.Level.BODY else FormatLogInterceptor.Level.NONE)
            builder.addInterceptor(netLogger)
        }?: run {
            if (BuildConfig.DEBUG) FormatLogInterceptor.Level.BODY else FormatLogInterceptor.Level.NONE
        }

        return builder.build()
    }

    open fun <Service> getService(serviceClass: Class<Service>, baseUrl: String): Service {
        return Retrofit.Builder()
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }
}