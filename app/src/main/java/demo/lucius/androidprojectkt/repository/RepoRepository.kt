package demo.lucius.androidprojectkt.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import demo.lucius.androidprojectkt.AppExecutors
import demo.lucius.androidprojectkt.api.ApiResponse
import demo.lucius.androidprojectkt.api.ApiSuccessResponse
import demo.lucius.androidprojectkt.api.GithubService
import demo.lucius.androidprojectkt.api.RepoSearchResponse
import demo.lucius.androidprojectkt.db.GithubDb
import demo.lucius.androidprojectkt.db.RepoDao
import demo.lucius.androidprojectkt.util.AbsentLiveData
import demo.lucius.androidprojectkt.util.RateLimiter
import demo.lucius.androidprojectkt.vo.Repo
import demo.lucius.androidprojectkt.vo.RepoSearchResult
import demo.lucius.androidprojectkt.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: GithubDb,
    private val repoDao: RepoDao,
    private val githubService: GithubService
) {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            override fun saveCallResult(item: List<Repo>) {
                repoDao.insertRepos(item)
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return repoDao.loadRepositories(owner)
            }

            override fun createCall(): LiveData<ApiResponse<List<Repo>>> {
                return githubService.getRepos(owner)
            }

            override fun onFetchFailed() {
                repoListRateLimit.reset(owner)
            }
        }.asLiveData()
    }


    fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo>(appExecutors) {
            override fun saveCallResult(item: Repo) {
                repoDao.insert(item)
            }

            override fun shouldFetch(data: Repo?) = data == null

            override fun loadFromDb() = repoDao.load(
                ownerLogin = owner,
                name = name
            )

            override fun createCall() = githubService.getRepo(
                owner = owner,
                name = name
            )
        }.asLiveData()
    }

    //
//    fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
//        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
//            override fun saveCallResult(item: List<Contributor>) {
//                item.forEach {
//                    it.repoName = name
//                    it.repoOwner = owner
//                }
//                db.runInTransaction {
//                    repoDao.createRepoIfNotExists(
//                        Repo(
//                            id = Repo.UNKNOWN_ID,
//                            name = name,
//                            fullName = "$owner/$name",
//                            description = "",
//                            owner = Repo.Owner(owner, null),
//                            stars = 0
//                        )
//                    )
//                    repoDao.insertContributors(item)
//                }
//            }
//
//            override fun shouldFetch(data: List<Contributor>?): Boolean {
//                return data == null || data.isEmpty()
//            }
//
//            override fun loadFromDb() = repoDao.loadContributors(owner, name)
//
//            override fun createCall() = githubService.getContributors(owner, name)
//        }.asLiveData()
//    }
//
    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            githubService = githubService,
            db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    /***
     * 用于进行搜索数据来源有两个方面，一个是Github 一个是数据库
     */
    fun search(query: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {

            override fun shouldFetch(data: List<Repo>?): Boolean = data == null

            override fun saveCallResult(item: RepoSearchResponse) {
                val repoIds = item.items.map { it.id }
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )

                //数据库事务处理
                db.runInTransaction {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(repoSearchResult)
                }
            }


            override fun loadFromDb(): LiveData<List<Repo>> {
                return Transformations.switchMap(repoDao.search(query)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        repoDao.loadOrdered(searchData.repoIds)
                    }
                }
            }

            override fun createCall() = githubService.searchRepos(query)

            override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>)
                    : RepoSearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }
        }.asLiveData()
    }
}