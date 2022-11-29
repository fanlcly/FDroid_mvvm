package com.jjl.mvvm.flog

import android.provider.ContactsContract.CommonDataKinds.StructuredName.SUFFIX

/**
 * @Description: (用一句话描述)
 * @author fanlei
 * @date  2022/11/28 21:41
 * @version V1.0
 */
object FLog {

    private const val DEFAULT_TAG = "FLog"
    private const val PARAM = "Param"
    private const val NULL = "null"
    private const val DEFAULT_MSG = "execute"
    private const val STACK_TRACE_INDEX_8 = 8
    private const val STACK_TRACE_INDEX_7 = 7
    private const val STACK_TRACE_INDEX_6 = 6

    const val JSON_INDENT = 4


    const val V = 0x1
    const val D = 0x2
    const val I = 0x3
    const val W = 0x4
    const val E = 0x5
    const val A = 0x6
    private const val JSON = 0x7

    private var mGlobalTag: String = ""

    private var IS_SHOW_LOG = true

    fun init(isShowLog: Boolean) {
        IS_SHOW_LOG = isShowLog
        mGlobalTag = DEFAULT_TAG
    }

    fun init(isShowLog: Boolean, tag: String?) {
        IS_SHOW_LOG = isShowLog
        mGlobalTag = tag ?: DEFAULT_TAG
    }


    fun v(objects: Any? = DEFAULT_MSG) {
        v(null, objects)
    }


    fun v(tag: String?, objects: Any?) {
        printLog(V, tag, objects)
    }


    fun d(objects: Any? = DEFAULT_MSG) {
        d(null, objects)
    }


    fun d(tag: String?, objects: Any?) {
        printLog(D, tag, objects)
    }


    fun i(objects: Any? = DEFAULT_MSG) {
        i(null, objects)
    }


    fun i(tag: String?, objects: Any?) {
        printLog(I, tag, objects)
    }


    fun w(objects: Any? = DEFAULT_MSG) {
        w(null, objects)
    }

    fun w(tag: String?, objects: Any?) {
        printLog(W, tag, objects)
    }


    fun e(objects: Any? = DEFAULT_MSG) {
        e(null, objects)
    }


    fun e(tag: String?, objects: Any?) {
        printLog(E, tag, objects)
    }


    fun a(objects: Any? = DEFAULT_MSG) {
        a(null, objects)
    }


    fun a(tag: String?, objects: Any?) {
        printLog(A, tag, objects)
    }


    fun json(tag: String?, jsonFormat: String?) {
        printLog(JSON, tag, jsonFormat)
    }

    fun debug(tag: String?, objects: Any?) {
        printLog(D, tag, objects, true)
    }


    @Synchronized
    private fun printLog(
        type: Int,
        tagStr: String?,
        objects: Any?,
        isDebug: Boolean = false
    ) {
        if ((!isDebug) && !IS_SHOW_LOG) {
            return
        }
        val tag = tagStr ?: mGlobalTag.ifEmpty { DEFAULT_TAG }
        val headString = buildHeader()
        val contents: String = buildContents(objects)
        when (type) {
            V, D, I, W, E, A -> BaseLog.printDefault(type, tag, headString + contents)
            JSON -> JsonLog.printJson(tag, contents, headString)
        }
    }


    /**
     * 构建头部信息
     */
    private fun buildHeader(): String {
        val stackTraceList = Thread.currentThread().stackTrace
        val stackTraceElement = stackTraceList.getOrElse(STACK_TRACE_INDEX_7) {
            stackTraceList.get(STACK_TRACE_INDEX_6)
        }

        var lineNumber = stackTraceElement.lineNumber
        val fileName = stackTraceElement.fileName
        val methodName = stackTraceElement.methodName

        if (lineNumber < 0) {
            lineNumber = 0
        }
        return "[($fileName:$lineNumber)#$methodName] "

    }


    /**
     * 构建内容信息
     */
    private fun buildContents(vararg objects: Any?): String {
        return if (objects.size > 1) {
            val stringBuilder = StringBuilder()
            stringBuilder.append("\n")
            for (i in objects.indices) {
                val obj = objects[i]
                stringBuilder.append(PARAM).append("[").append(i)
                    .append("]").append(" = ").append(obj.toString()).append("\n")
            }
            stringBuilder.toString()
        } else if (objects.size == 1) {
            val obj = objects[0]
            obj.toString()
        } else {
            NULL
        }
    }

}