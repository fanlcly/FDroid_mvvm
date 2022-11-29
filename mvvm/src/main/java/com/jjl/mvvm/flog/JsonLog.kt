package com.jjl.mvvm.flog

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @Description: (JsonLog)
 * @author fanlei
 * @date  2022/11/28 22:00
 * @version V1.0
 */
object JsonLog {
    fun printJson(tag: String?, msg: String?, isShowTopLine: Boolean = true, isShowBottomLine: Boolean = true) {
        val message: String? = msg?.let {
            try {
                if (it.startsWith("{")) {
                    val jsonObject = JSONObject(it)
                    jsonObject.toString(FLog.JSON_INDENT)
                } else if (it.startsWith("[")) {
                    val jsonArray = JSONArray(it)
                    jsonArray.toString(FLog.JSON_INDENT)
                } else {
                    it
                }
            } catch (e: JSONException) {
                it
            }
        }

        if (isShowTopLine) {
            printLine(tag, true)
        }
        val lines: Array<String>? = message?.split(lineSeparator())?.toTypedArray()
        lines?.let {
            for (line in it) {
                Log.d(tag, "║ $line")
            }
        }
        if (isShowBottomLine) {
            printLine(tag, false)
        }

    }
}