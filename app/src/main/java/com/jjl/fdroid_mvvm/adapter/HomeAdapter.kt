package com.jjl.fdroid_mvvm.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jjl.fdroid_mvvm.R
import com.jjl.fdroid_mvvm.entity.SearchResponse

/**
 * @Description: (HomeAdapter)
 * @author fanlei
 * @date  2022/11/25 21:01
 * @version V1.0
 */
class HomeAdapter(datas: MutableList<SearchResponse>?) :
    BaseQuickAdapter<SearchResponse, BaseViewHolder>(R.layout.item_home, datas) {
    override fun convert(holder: BaseViewHolder, item: SearchResponse) {
        holder.setText(R.id.tv_name, item.name)
    }

}