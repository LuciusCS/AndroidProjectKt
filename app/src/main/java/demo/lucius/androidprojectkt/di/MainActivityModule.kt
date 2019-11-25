package demo.lucius.androidprojectkt.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import demo.lucius.androidprojectkt.MainActivity

@Suppress("unused")
@Module
abstract class MainActivityModule{
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity():MainActivity

}