package demo.lucius.androidprojectkt.db

import androidx.room.Database
import androidx.room.RoomDatabase
import demo.lucius.androidprojectkt.vo.Contributor
import demo.lucius.androidprojectkt.vo.Repo
import demo.lucius.androidprojectkt.vo.RepoSearchResult
import demo.lucius.androidprojectkt.vo.User

/**
 * 用于表示数据库保存
 */

@Database(
    entities = [User::class, Repo::class, Contributor::class, RepoSearchResult::class],
    version = 1,
    exportSchema = false
)
abstract class GithubDb : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao
}