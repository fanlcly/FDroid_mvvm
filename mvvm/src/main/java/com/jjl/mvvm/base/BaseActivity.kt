package com.jjl.mvvm.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.permissionx.guolindev.PermissionX

/**
 * @Description: (BaseActivity)
 * @author fanlei
 * @date  2022/11/17 20:27
 * @version V1.0
 */
abstract class BaseActivity<VB : ViewBinding>(private val inflate: (LayoutInflater) -> VB) :
    AppCompatActivity(),IView {

    lateinit var binding: VB

    protected val mActivity: Activity
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    abstract fun initView()


    //请求一些必须要的权限
    protected open fun requestPermission(vararg permission: String, reason: String? = "") {
        PermissionX.init(this)
            .permissions(*permission)
            .onExplainRequestReason { scope, deniedList ->
                val message = if (reason.isNullOrEmpty()) "应用需要您同意以下权限才能正常使用" else reason
                scope.showRequestReasonDialog(deniedList, message, "同意", "拒绝")
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) { // 所有申请的权限都已通过
                    onPermissionSuccess()
                } else {
                    onPermissionFail(deniedList)
                }
            }

    }


    /**
     * 权限申请成功执行方法
     */
    protected open fun onPermissionSuccess() {

    }

    /**
     * 权限申请失败
     */
    protected open fun onPermissionFail(deniedList: MutableList<String>) {

    }


}