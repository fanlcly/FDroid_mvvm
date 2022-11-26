package com.jjl.fdroid_mvvm.vm

import androidx.lifecycle.MutableLiveData
import com.jjl.fdroid_mvvm.entity.SearchResponse
import com.jjl.fdroid_mvvm.net.HttpClient
import com.jjl.fdroid_mvvm.net.stateCallback.ListDataUiState
import com.jjl.mvvm.base.BaseViewModel

/**
 * @Description: (HomeFViewModel)
 * @author fanlei
 * @date  2022/11/24 21:21
 * @version V1.0
 */
class HomeFViewModel : BaseViewModel() {

    var dataState = MutableLiveData<ListDataUiState<SearchResponse>>()

    suspend fun getHotData() {
        handleResponse(HttpClient.service::getSearchData, {
            val listDataUiState =
            ListDataUiState(isSuccess = true, listData = it)
            dataState.value = listDataUiState
        }, {
            val listDataUiState =
                ListDataUiState<SearchResponse>(isSuccess = false, errMessage = it.errorMsg ?: "")
            dataState.value = listDataUiState
        })
    }

}