# viewModel + liveData + 协程 + flow 带你快速开发

## 简介

工作之余用协程整了一套网络请求框架，先分享给大家。其实大家不要害怕，我们retrofit用的那么熟了，协程只不过是在原来的基础上作了一些调整而已，下面有介绍用法

### base基类

### 网络请求

### log日志输出

注意：项目并没有按谷歌官方文档的思想开发，由于在实际开发过程中，几乎都是单数据来源，故删掉了Repository层;网络层的Log输出已经格式化

## 使用

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



## 感谢

感谢mvvm优秀的作者们，项目会长期不定时更新优化...

