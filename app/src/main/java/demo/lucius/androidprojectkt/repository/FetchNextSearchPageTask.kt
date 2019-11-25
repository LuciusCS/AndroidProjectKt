package demo.lucius.androidprojectkt.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import demo.lucius.androidprojectkt.api.*
import demo.lucius.androidprojectkt.db.GithubDb
import demo.lucius.androidprojectkt.vo.RepoSearchResult
import demo.lucius.androidprojectkt.vo.Resource
import java.io.IOException

/**
 * 从数据库中读取搜索结果，如果有下一页搜索结果，则读取下一页搜索结果
 */
class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: GithubService, private val db: GithubDb
) : Runnable {

    private val _livedata = MutableLiveData<Resource<Boolean>>()

    val liveData: LiveData<Resource<Boolean>> = _livedata


    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //用于表示从数据库中保存的库的信息，这里的返回值是List?????
        val current = db.repoDao().findSearchResult(query)
        if (current == null) {
            _livedata.postValue(null)
            return
        }

        val nextPage = current.next

        if (nextPage == null) {
            _livedata.postValue(Resource.success(false))
            return
        }

        val newValue = try {
            val response = githubService.searchRepos(query, nextPage).execute()
            val apiResponse = ApiResponse.create(response)
            /**
             * Kotlin中的when关键字与Java中的Switch关键字类似
             */
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    /**
                     * 将所有的仓库的id 添加到一个list中
                     */
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.repoIds)
                    /**
                     * items是一个Repo的list, it代表一个repo
                     */
                    ids.addAll(apiResponse.body.items.map { it.id })

                    val merged =
                        RepoSearchResult(query, ids, apiResponse.body.total, apiResponse.nextPage)

                    db.runInTransaction {
                        db.repoDao().insert(merged)
                        db.repoDao().insertRepos(apiResponse.body.items)
                    }
                    Resource.success(apiResponse.nextPage != null)
                }

                is ApiEmptyResponse -> {
                    Resource.success(false)
                }

                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }

            }
        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }

        _livedata.postValue(newValue)
    }
}