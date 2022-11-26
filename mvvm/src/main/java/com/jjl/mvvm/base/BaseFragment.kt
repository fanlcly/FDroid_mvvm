package com.jjl.mvvm.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.permissionx.guolindev.PermissionX

/**
 * @Description: (BaseFragment)
 * @author fanlei
 * @date  2022/11/17 21:40
 * @version V1.0
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    protected open val mActivity: Activity
        get() = requireActivity()

    private var _binding: VB? = null
    protected open val binding: VB get() = _binding ?: throw IllegalAccessException("请初始化你的布局")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        buildObserve()
    }

    protected open fun buildObserve() {
    }

    abstract fun initView()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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