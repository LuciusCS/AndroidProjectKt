package demo.lucius.androidprojectkt.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import demo.lucius.androidprojectkt.ui.repo.RepoFragment
import demo.lucius.androidprojectkt.ui.search.SearchFragment
import demo.lucius.androidprojectkt.ui.user.UserFragment


@Module
abstract class  FragmentBuildersModule{

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment():RepoFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment():UserFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

}