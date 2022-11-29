package com.jjl.fdroid_mvvm

import android.app.Application
import com.jjl.mvvm.flog.FLog

/**
 * @Description: (用一句话描述)
 * @author fanlei
 * @date  2022/11/29 16:07
 * @version V1.0
 */
class BaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        FLog.init(true,"mvvm")
    }
}