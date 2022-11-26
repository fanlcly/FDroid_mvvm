package com.jjl.mvvm.net.interceptor

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okio.GzipSource
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.EOFException
import java.io.IOException
import java.nio.Buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.charset.UnsupportedCharsetException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @Description: (FormatLogInterceptor)
 * @author fanlei
 * @date  2022/11/19 19:35
 * @version V1.0
 */
class FormatLogInterceptor(private var logger: Logger) : Interceptor {


    @Volatile
    private var headersToRedact = emptySet<String>()

    @set:JvmName("level")
    @Volatile
    var level = Level.NONE

    enum class Level {
        /**
         * 没有日志
         */
        NONE,

        /**
         * 记录请求和响应行。
         *
         *
         * 例：
         * --> POST /greeting http/1.1 (3-byte body)
         * 例：
         * <-- 200 OK (22ms, 6-byte body)
         */
        BASIC,


        /**
         * 记录请求和响应行及其各自的标头。
         *
         *
         * 例：
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         *
         * 例：
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         */
        HEADERS,

        /**
         * 记录请求和响应行及其各自的头和主体（如果存在）。
         *
         *
         * 例：
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         *
         * 例：
         * --> END POST
         *
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         *
         * Hello!
         * <-- END HTTP
         */
        BODY
    }

    fun interface Logger {
        fun log(message: String)

        companion object {
            /** A [Logger] defaults output appropriate for the current platform. */
            @JvmField
            val DEFAULT: Logger = DefaultLogger()

            private class DefaultLogger : Logger {
                override fun log(message: String) {
                    Platform.get().log(message)
                }
            }
        }
    }

    fun redactHeader(name: String) {
        val newHeadersToRedact = TreeSet(String.CASE_INSENSITIVE_ORDER)
        newHeadersToRedact += headersToRedact
        newHeadersToRedact += name
        headersToRedact = newHeadersToRedact
    }

    /**
     * Sets the level and returns this.
     *
     * This was deprecated in OkHttp 4.0 in favor of the [level] val. In OkHttp 4.3 it is
     * un-deprecated because Java callers can't chain when assigning Kotlin vals. (The getter remains
     * deprecated).
     */
    fun setLevel(level: Level) = apply {
        this.level = level
    }

    @JvmName("-deprecated_level")
    @Deprecated(
        message = "moved to var",
        replaceWith = ReplaceWith(expression = "level"),
        level = DeprecationLevel.ERROR
    )
    fun getLevel(): Level = level

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val requestBody = request.body
        val connection = chain.connection()

        val requestBuffer = StringBuilder()

        var requestStartMessage =
            ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        if (!logHeaders && requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        requestBuffer.append(requestStartMessage)

        if (logHeaders) {
            val headers = request.headers

            if (requestBody != null) {
                // Request body headers are only present when installed as a network interceptor. When not
                // already present, force them to be included (if available) so their values are known.
                requestBody.contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        requestBuffer.append("Content-Type: ").append(it).append("\n")
                    }
                }
                if (requestBody.contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        requestBuffer.append("Content-Length: ").append(requestBody.contentLength())
                            .append("\n")
                    }
                }
            }

            for (i in 0 until headers.size) {
                logHeader(headers, i, requestBuffer)
            }

            if (!logBody || requestBody == null) {
                requestBuffer.append("--> END ").append(request.method).append("\n")
            } else if (bodyHasUnknownEncoding(request.headers)) {
                requestBuffer.append("--> END ${request.method} (encoded body omitted)")
                    .append("\n")
            } else if (requestBody.isDuplex()) {
                requestBuffer.append("--> END ${request.method} (duplex request body omitted)")
                    .append("\n")
            } else if (requestBody.isOneShot()) {
                requestBuffer.append("--> END ${request.method} (one-shot body omitted)")
                    .append("\n")
            } else {
                val buffer = okio.Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset =
                    contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                requestBuffer.append("")
                if (buffer.isProbablyUtf8()) {
                    requestBuffer.append(buffer.readString(charset)).append("\n")
                    requestBuffer.append("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
                } else {
                    requestBuffer.append("--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)")
                }
            }
        }
        logger.log(requestBuffer.toString())

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBuilder = StringBuilder()
        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        responseBuilder.append("<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms${if (!logHeaders) ", $bodySize body" else ""})")
            .append("\n")

        if (logHeaders) {
            val headers = response.headers
            for (i in 0 until headers.size) {
                logHeader(headers, i, responseBuilder)
            }

            if (!logBody || !response.promisesBody()) {
                responseBuilder.append("<-- END HTTP").append("\n")
            } else if (bodyHasUnknownEncoding(response.headers)) {
                responseBuilder.append("<-- END HTTP (encoded body omitted)").append("\n")
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = okio.Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset =
                    contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                if (!buffer.isProbablyUtf8()) {
                    responseBuilder.append("")
                    responseBuilder.append("<-- END HTTP (binary ${buffer.size}-byte body omitted)")
                    return response
                }

                if (contentLength != 0L) {
                    responseBuilder.append("")
                    responseBuilder.append(buffer.clone().readString(charset))
                }

                if (gzippedLength != null) {
                    responseBuilder.append("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
                } else {
                    responseBuilder.append("<-- END HTTP (${buffer.size}-byte body)")
                }
            }
        }
        logger.log(responseBuilder.toString())
        return response
    }

    private fun logHeader(headers: Headers, i: Int, stringBuilder: StringBuilder) {
        val value = if (headers.name(i) in headersToRedact) "██" else headers.value(i)
        stringBuilder.append(headers.name(i)).append(":").append(value).append("\n")
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}