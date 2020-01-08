package demo.lucius.androidprojectkt.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import demo.lucius.androidprojectkt.api.GithubService
import demo.lucius.androidprojectkt.db.GithubDb
import demo.lucius.androidprojectkt.db.RepoDao
import demo.lucius.androidprojectkt.db.UserDao
import demo.lucius.androidprojectkt.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideGithubService(): GithubService {
        /**
         * class.java传入的是Java类
         */
        return Retrofit.Builder().baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())               //设置数据解析器
            .addCallAdapterFactory(LiveDataCallAdapterFactory()).build()
            .create(GithubService::class.java)                               //创建网络请求接口的实例
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): GithubDb {
        return Room.databaseBuilder(app, GithubDb::class.java, "github.db")
            .fallbackToDestructiveMigration().build();
    }

    @Singleton
    @Provides
    fun provideUserDao(db: GithubDb): UserDao {
        return db.userDao()
    }


    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDb): RepoDao {
        return db.repoDao()
    }
}