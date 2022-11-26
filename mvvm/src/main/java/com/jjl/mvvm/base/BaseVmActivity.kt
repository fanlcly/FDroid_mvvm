package com.jjl.mvvm.base

import android.app.ProgressDialog
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

/**
 * @Description: (BaseVmActivity)
 * @author fanlei
 * @date  2022/11/20 10:24
 * @version V1.0
 */
abstract class BaseVmActivity<VB : ViewBinding>(inflate: (LayoutInflater) -> VB) :
    BaseActivity<VB>(inflate) {

    private var progressDialog: ProgressDialog? = null

    override fun showLoading() {
        if (progressDialog == null)
            progressDialog = ProgressDialog(this)
        progressDialog?.show()
    }

    override fun dismissLoading() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()
    }
}