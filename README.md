# viewModel + liveData + 协程 + flow 带你快速开发

## 简介

工作之余用协程整了一套网络请求框架，先分享给大家。其实大家不要害怕，我们retrofit用的那么熟了，协程只不过是在原来的基础上作了一些调整而已，下面有介绍用法

注意：项目100%使用kotlin开发，如直接依赖，java项目可能无法使用；项目并没有按谷歌官方文档的思想开发，由于在实际开发过程中，几乎都是单数据来源，故删掉了Repository层;网络层的Log输出已经格式化

## 使用说明

### 说明

你以前在定义ApiService的时候可能是这样的：

  ~~~
        interface ApiService {
            @GET("Genre/json")
            fun queryGenre() : Call<DataEntity>
        }
  ~~~

或者是这样：

  ~~~
        interface ApiService {
            @GET("Genre/json")
            fun queryGenre() : Observable<DataEntity>
        }
  ~~~

#### 现在只需要把返回类型改一下就好，因为retrofit2.6以后兼容了协程，第一我们的返回类型可以直接用数据实体接收，第二协程的线程调度已经帮我完成了，我们只管做操作就行了；接下来在方法上加上suspend关键字提醒调用者这个方法有可能有耗时操作

  ~~~
        interface ApiService {
            @GET("Genre/json")
          suspend  fun queryGenre() : DataEntity
        }
  ~~~

你以前在发起请求时可能是这样的：

 ~~~
        val service = HttpClient.service(ApiService::class.java,baseUrl)
        service.queryGenre().enqueue(object : Callback<DataEntity> {
            override fun onFailure(call: Call<DataEntity>, t: Throwable) {
                TODO("not implemented") 
            }
            override fun onResponse(call: Call<DataEntity>, response: Response<DataEntity>) {
                TODO("not implemented") 
            }
        })
 ~~~

#### 现在只需要一句话就能搞定

~~~
      HttpClient.service.queryGenre()
 ~~~

#### 只不过实际开发过程中我们需要处理异常和请求失败的情况，而且还有loading框的处理，现在我们用flow来处理loading框，用runCatching来处理异常和失败的情况

~~~
 /**
 * flow封装loading框
 */
fun IView.launchRequest(requestBlock: suspend () -> Unit, isLoading: Boolean = true) {
    lifecycleScope.launch {
        flow {
            emit(requestBlock())
        }.onStart {
            if (isLoading) {
                showLoading()
            }
        }.onCompletion {
            dismissLoading()
        }.collect()
    }
}
 ~~~

~~~
     /**
     * 处理请求信息
     */
    suspend fun <T> handleResponse(
        block: suspend () -> BaseResponse<T>,
        success: (T?) -> Unit,
        error: (AppException) -> Unit = {},
    ) {
        runCatching {
            block.invoke()
        }.onSuccess { response ->
            executeResult(response, success, error)
        }.onFailure { e ->
            e.printStackTrace()
            error(ExceptionResult.handleException(e))
        }
    }
 ~~~

然后就是异常情况的处理，这里就不帖代码了，想要了解的可以直接下demo。

### 使用

首先 if(android studio 版本 >4.0) {   
将其添加到根setting.gradle中。

~~~
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
~~~

} else {   
将其添加到根build.gradle中。

~~~
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
~~~

}

然后，再你的module中增加依赖

~~~
dependencies {
            ...
	        implementation 'com.github.fanlcly:FDroid_mvvm:1.0.0'
	        ...
	}
~~~

由于以下api已经下放，所以不需要在你的项目中重复添加

~~~
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:logging-interceptor:4.10.0")

    api "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0"
    api "androidx.lifecycle:lifecycle-livedata-ktx:2.4.0"
    api "androidx.lifecycle:lifecycle-runtime-ktx:2.5.0"

    api "androidx.fragment:fragment-ktx:1.5.0"
    api "androidx.activity:activity-ktx:1.5.0"
~~~

接下来就是在项目中的使用了

首先你定义好你的ApiService接口类

~~~
interface ApiService {
    
       @GET("Genre/json")
      suspend  fun queryGenre() : DataEntity
}
~~~

然后继承BaseHttpClient类，通过baseUrl创建ApiService实例，当然你也可以通过MyNetProvider来配置你的head拦截器或者自定义的拦截器

~~~
object HttpClient : BaseHttpClient() {
    // 如果不需要配置可以不用重写
    override var provider: NetProvider? = MyNetProvider()

    val service by lazy { getService(ApiService::class.java, ApiService.BASE_URL) }
}
~~~

如果配置你自己的provider项需继承NetProvider类

~~~
class MyNetProvider : NetProvider {
    override fun configConnectTimeoutMills(): Long {
        return 15
    }

    override fun configReadTimeoutMills(): Long {
        return 15
    }

    override fun configInterceptors(): Array<Interceptor>? {
        return null
    }

    override fun configLogEnable(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun configHeader(): RequestHeader? {
        return null
    }
}
~~~

当然根据你自己的服务端返回的数据定义好responseModel是必不可少的，这里需要继承BaseResponse

~~~
data class ResponseModel<T>(val errorCode: Int? = null,val errorMsg: String? = null,val data: T? = null) 
:BaseResponse<T>() {

    //  服务端返回的code为0 就代表请求成功，请你根据自己的业务需求来改变
    override fun isSuccess() = errorCode == 0

    override fun getResponseCode() = errorCode ?: 0

    override fun getResponseData() = data

    override fun getResponseMsg() = errorMsg ?: ""

}
~~~

然后就是在项目中调用了

~~~

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
~~~

到这里差不多就结束了，项目代码其实不多，建议大家下载下来自己根据实际情况做修改，如果感觉还可以的话，点个赞就很满足了。希望各位大佬指正，深表感谢。

## 感谢

感谢mvvm优秀的作者们，项目会长期不定时更新优化...

