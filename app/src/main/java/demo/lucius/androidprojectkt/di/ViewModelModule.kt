package demo.lucius.androidprojectkt.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import demo.lucius.androidprojectkt.ui.repo.RepoViewModel
import demo.lucius.androidprojectkt.ui.search.SearchViewModel
import demo.lucius.androidprojectkt.ui.user.UserViewModel
import demo.lucius.androidprojectkt.viewmodel.GithubViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule{
//    @Binds
//    @IntoMap
//    @ViewModelKey(UserViewModel::class)
//    abstract fun bindUserViewModel(userViewModel: UserViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(RepoViewModel::class)
//    abstract fun bindRepoViewModel(repoViewModel: RepoViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: GithubViewModelFactory): ViewModelProvider.Factory
}