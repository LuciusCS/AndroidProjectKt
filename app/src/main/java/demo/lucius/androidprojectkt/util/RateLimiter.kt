package demo.lucius.androidprojectkt.util

import android.os.SystemClock
import androidx.collection.ArrayMap
import java.util.concurrent.TimeUnit

/**
 * 用于决定是否应该fetch一部分数据
 */
class RateLimiter<in KEY>(timeout:Int,timeUnit:TimeUnit) {

    private val timestamps= ArrayMap<KEY,Long>()

    private val timeout=timeUnit.toMillis(timeout.toLong())

    fun shouldFetch(key:KEY):Boolean{
        val lastFetched=timestamps[key]
        val now=now()
        if (lastFetched==null){
            timestamps[key]=now
            return true
        }

        if (now-lastFetched>timeout){
            timestamps[key]=now
            return true
        }
        return false
    }
    private fun now()=SystemClock.uptimeMillis()

    fun reset(key: KEY){
        timestamps.remove(key)
    }
}