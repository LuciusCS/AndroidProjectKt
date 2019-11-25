package demo.lucius.androidprojectkt

import android.os.Handler
import android.os.Looper
import java.security.spec.ECField
import java.util.concurrent.Executor
import java.util.concurrent.Executors

import javax.inject.Inject
import javax.inject.Singleton


/**
 *  用于整个应用的线程池
 *
 */
@Singleton
open class AppExecutors(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: Executor
) {
    @Inject
    constructor():this(Executors.newSingleThreadExecutor(),Executors.newFixedThreadPool(3),MainThreadExector())

    fun diskIO():Executor{
        return diskIO
    }

    fun networkIO():Executor{
        return networkIO
    }

    fun mainThread():Executor{
        return mainThread
    }



    private class MainThreadExector:Executor{

        private val mainThreadHandler= Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }
}