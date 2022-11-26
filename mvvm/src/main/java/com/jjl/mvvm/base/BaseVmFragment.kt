package com.jjl.mvvm.base

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * @Description: (BaseVmFragment)
 * @author fanlei
 * @date  2022/11/24 20:46
 * @version V1.0
 */
abstract class BaseVmFragment<VB : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    BaseFragment<VB>(inflate),IView {

    private var progressDialog: ProgressDialog? = null

    override fun showLoading() {
        if (progressDialog == null)
            progressDialog = ProgressDialog(requireActivity())
        progressDialog?.show()
    }

    override fun dismissLoading() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()
    }
}