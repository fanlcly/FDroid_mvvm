package com.jjl.fdroid_mvvm.net

import com.jjl.fdroid_mvvm.entity.GenreEntity
import com.jjl.fdroid_mvvm.entity.SearchResponse
import retrofit2.http.GET

/**
 * @Description: 由于两个接口来自不同的平台，所以用了两个responseModel,正常情况在单服务器环境下，只需要一个model就行了。
 * @author fanlei
 * @date  2022/11/26 15:16
 * @version V1.0
 */
interface ApiService {

    companion object {
        const val BASE_URL = "https://wanandroid.com/"
    }


    /**
     * 获取热门搜索数据
     */
    @GET("https://wanandroid.com/hotkey/json")
    suspend fun getSearchData(): ResponseModel<MutableList<SearchResponse>>


    /**
     * 获取年级数据
     */
    @GET("http://www.guangjieapp.com:8001/intelligence-api/tComposition/Genre/queryCompositionGenre")
    suspend fun queryCompositionGenre(): FResponseModel<MutableList<GenreEntity>>

}