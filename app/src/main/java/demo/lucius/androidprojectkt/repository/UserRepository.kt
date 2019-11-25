package demo.lucius.androidprojectkt.repository

import androidx.lifecycle.LiveData

import demo.lucius.androidprojectkt.AppExecutors
import demo.lucius.androidprojectkt.api.ApiResponse
import demo.lucius.androidprojectkt.api.GithubService
import demo.lucius.androidprojectkt.db.UserDao
import demo.lucius.androidprojectkt.vo.Resource
import demo.lucius.androidprojectkt.vo.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubService: GithubService
) {
    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                userDao.insert(item)
            }

            //            override fun shouldFetch(data: User?): Boolean {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//
//            }
            override fun shouldFetch(data: User?) = data == null


            //            override fun loadFromDb(): LiveData<User> {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
            override fun loadFromDb(): LiveData<User> = userDao.findByLogin(login)
//
//            override fun createCall(): LiveData<ApiResponse<User>> {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }

            override fun createCall(): LiveData<ApiResponse<User>> =githubService.getUser(login)

        }.asLiveData()
    }
}