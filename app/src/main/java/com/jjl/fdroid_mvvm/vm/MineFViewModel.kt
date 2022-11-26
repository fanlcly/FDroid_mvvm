package com.jjl.fdroid_mvvm.vm

import androidx.lifecycle.MutableLiveData
import com.jjl.fdroid_mvvm.entity.GenreEntity
import com.jjl.fdroid_mvvm.net.HttpClient
import com.jjl.mvvm.base.BaseViewModel
import com.jjl.mvvm.ext.handleParseResponse
import com.jjl.mvvm.net.response.ResponseState

/**
 * @Description: (MineFViewModel)
 * @author fanlei
 * @date  2022/11/26 12:16
 * @version V1.0
 */
class MineFViewModel : BaseViewModel() {
    var genreResult = MutableLiveData<ResponseState<MutableList<GenreEntity>>>()

    suspend fun queryCompositionGenre() {
        handleParseResponse(HttpClient.service::queryCompositionGenre , genreResult)
    }
}