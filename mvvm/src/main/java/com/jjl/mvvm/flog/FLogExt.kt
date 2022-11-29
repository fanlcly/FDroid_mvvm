package com.jjl.mvvm.flog

import android.util.Log

/**
 * @Description: (JsonLog的扩展类)
 * @author fanlei
 * @date  2022/11/28 22:03
 * @version V1.0
 */
fun JsonLog.printLine(tag: String?, isTop: Boolean) {
    if (isTop) {
        Log.d(
            tag,
            "╔═══════════════════════════════════════════════════════════════════════════════════════"
        )
    } else {
        Log.d(
            tag,
            "╚═══════════════════════════════════════════════════════════════════════════════════════"
        )
    }
}

fun FLog.lineSeparator(): String {
    return System.getProperty("line.separator") ?: "\r\n"
}

fun <T> T.log() {
    FLog.d(this)
}



