package demo.lucius.androidprojectkt.api

import retrofit2.Response
import timber.log.Timber
import java.lang.NumberFormatException
import java.util.regex.Pattern

/**
 * 用于API响应的统一类
 *
 * sealed  密封类用来表示受限的类继承结构：当一个值为有限几种的类型、而不能有任何其他类型时。
 * 在某种意义上，他们是枚举类的扩展：枚举类型的值集合也是受限的，但每个枚举常量只存在一个实例，
 * 而密封类的一个子类可以有可包含状态的多个实例
 *
 */
sealed class ApiResponse<T> {


    /**
     * 使用object声明类对象，是一种单例模式？ 称为对象声明，是线程安全的
     *
     * 伴生对象： 类内部的对象声明使用compaaion关键字标记
     */
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body = body, linkHeader = response.headers()?.get("link"))
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error");
            }
        }

    }

}

/**
 * 用于HTTP 204 信息返回
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T, val links: Map<String, String>) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1))
                } catch (ex: NumberFormatException) {
                    Timber.w("cannot parse next page from %s", next)
                    null
                }
            }

        }

    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {

            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this);

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }

    }


}

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()

//data class ApiSuccessResponse<T>(val body: T,val links: Map<String, String>):ApiResponse<T>(){
//
//}