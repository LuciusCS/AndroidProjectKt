package demo.lucius.androidprojectkt.vo

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

/**
 * data class就是一个类中只包含一些数据字段，类似于vo,pojo,java bean
 */
@Entity(primaryKeys = ["login"])
data class User(
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("company")
    val company: String?,
    @field:SerializedName("repos_url")
    val reposUrl: String?,
    @field:SerializedName("blog")
    val blog: String?

)