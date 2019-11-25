package demo.lucius.androidprojectkt.util

import androidx.lifecycle.LiveData
import demo.lucius.androidprojectkt.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 将Call转换为一个ApiResponse类型的Livedata
 */
class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiResponse<R>>> {


    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {

            //AtomicBoolean保持变量的原子性
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                //如果值是expect compareAndSet将started设置为update，并执行后面的语句，整个过程是不能够被打断的
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onFailure(call: Call<R>, t: Throwable) {
                            //是object:LiveData<ApiResponse<R>>调用的
                            postValue(ApiResponse.create(t))
                        }

                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(ApiResponse.create(response))
                        }
                    })
                }
            }
        }
    }

    override fun responseType() = responseType
}