package com.jjl.mvvm.flog

import android.util.Log


/**
 * @Description: (BaseLog)
 * @author fanlei
 * @date  2022/11/28 21:32
 * @version V1.0
 */
class BaseLog {
    companion object {
        private const val MAX_LENGTH = 4000

        fun printDefault(type: Int, tag: String, msg: String) {
            var index = 0
            val length: Int = msg.length
            val count: Int = length / MAX_LENGTH

            if (count > 0) {
                for (i in 0 until count) {
                    val sub: String =
                        msg.substring(index, index + MAX_LENGTH)
                    printSub(type, tag, sub)
                    index += MAX_LENGTH
                }
                printSub(type, tag, msg.substring(index, length))
            } else {
                printSub(type, tag, msg)
            }
        }

        private fun printSub(type: Int, tag: String, msg: String) {
            when (type) {
                FLog.V -> Log.v(tag, msg)
                FLog.D -> Log.d(tag, msg)
                FLog.I -> Log.i(tag, msg)
                FLog.W -> Log.w(tag, msg)
                FLog.E -> Log.e(tag, msg)
                FLog.A -> Log.wtf(tag, msg)
            }
        }
    }
}