package demo.lucius.androidprojectkt.api

import com.google.gson.annotations.SerializedName
import demo.lucius.androidprojectkt.vo.Repo

/**
 * 用于表示repo搜索的请求类型
 */
data class RepoSearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<Repo>
) {
    var nextPage: Int? = null
}