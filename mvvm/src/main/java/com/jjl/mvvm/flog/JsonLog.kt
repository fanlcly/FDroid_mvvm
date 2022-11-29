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
    fun printJson(tag: String?, msg: String?, headString: String?) {
        var message: String? = msg?.let {
            try {
                if (it.startsWith("{")) {
                    val jsonObject = JSONObject(msg)
                    jsonObject.toString(FLog.JSON_INDENT)
                } else if (it.startsWith("[")) {
                    val jsonArray = JSONArray(msg)
                    jsonArray.toString(FLog.JSON_INDENT)
                } else {
                    msg
                }
            } catch (e: JSONException) {
                msg
            }
        }

        printLine(tag, true)
        message = headString + FLog.lineSeparator() + message
        val lines: Array<String> = message.split(FLog.lineSeparator()).toTypedArray()
        for (line in lines) {
            Log.d(tag, "║ $line")
        }
        printLine(tag, false)
    }
}