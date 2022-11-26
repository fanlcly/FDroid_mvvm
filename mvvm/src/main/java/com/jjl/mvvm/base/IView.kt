package com.jjl.mvvm.base

import androidx.lifecycle.LifecycleOwner

/**
 * @Description: (IView)
 * @author fanlei
 * @date  2022/11/17 20:34
 * @version V1.0
 */
interface IView: LifecycleOwner {

    fun showLoading()

    fun dismissLoading()
}