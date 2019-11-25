package demo.lucius.androidprojectkt.di

import android.app.Activity
import android.app.Application

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.HasActivityInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import demo.lucius.androidprojectkt.GithubApp

/**
 * 当一个class实现[injectable]接口时，可以自动注入fragments
 *
 * object关键字可以实现Kotlin的单例模式，可以定义在全局中，也可以定义在类的内部
 * object定义后会直接实例化，不能有构造函数
 * 定义在类内部的object不能访问类的成员
 */

object AppInjector {
    fun init(githubApp: GithubApp) {

        DaggerAppComponent.builder().application(githubApp).build().inject(githubApp)

        /**
         * 在这里的object使用类似于Java中的匿名内部类
         */
        githubApp.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                handleActivity(activity)
            }

            override fun onActivityResumed(activity: Activity) {

            }

        })

    }

    private fun handleActivity(activity: Activity) {

        /**
         * is 类似于Java中的instanceof
         */
        if (activity is HasSupportFragmentInjector) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    savedInstanceState: Bundle?
                ) {
                    if (f is Injectable) {
                        AndroidSupportInjection.inject(f);
                    }
//                    super.onFragmentCreated(fm, f, savedInstanceState)
                }
            }, true)
        }
    }


}