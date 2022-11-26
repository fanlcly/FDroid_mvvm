package com.jjl.fdroid_mvvm.entity


/**
 * 搜索热词
 */
data class SearchResponse(
    var id: Int,
    var link: String,
    var name: String,
    var order: Int,
    var visible: Int
)
