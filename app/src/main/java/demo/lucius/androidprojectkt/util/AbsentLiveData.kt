package demo.lucius.androidprojectkt.util

import androidx.lifecycle.LiveData


/**
 * 用于表示livedata数据 并含有一个空值
 */
class AbsentLiveData<T :Any?> private constructor():LiveData<T>(){

    init {
        postValue(null)
    }

    companion object{
        fun <T>create():LiveData<T>{
            return  AbsentLiveData()
        }
    }


}