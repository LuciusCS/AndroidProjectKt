package demo.lucius.androidprojectkt.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import demo.lucius.androidprojectkt.repository.RepoRepository
import demo.lucius.androidprojectkt.repository.UserRepository
import demo.lucius.androidprojectkt.util.AbsentLiveData
import demo.lucius.androidprojectkt.vo.Repo
import demo.lucius.androidprojectkt.vo.Resource
import javax.inject.Inject

class UserViewModel @Inject constructor(
    userRepository: UserRepository,
    repoRepository: RepoRepository
) : ViewModel() {
    private val _login = MutableLiveData<String>()

    val login:LiveData<String>
    get() = _login

    val repositories: LiveData<Resource<List<Repo>>> = Transformations
        .switchMap(_login) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repoRepository.loadRepos(login)
            }
        }

}