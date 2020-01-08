package demo.lucius.androidprojectkt.ui.search

import androidx.lifecycle.*
import androidx.lifecycle.Observer

import demo.lucius.androidprojectkt.repository.RepoRepository
import demo.lucius.androidprojectkt.util.AbsentLiveData
import demo.lucius.androidprojectkt.vo.Repo
import demo.lucius.androidprojectkt.vo.Resource
import demo.lucius.androidprojectkt.vo.Status
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(repoRepository: RepoRepository) : ViewModel() {

    private val _query = MutableLiveData<String>()

    private val nextPageHandler = NextPageHandler(repoRepository)

    val query: LiveData<String> = _query

    /**
     * Transformations.switchMap  result监听_query的变化，并将 _query转化为适合自己的
     */
    val results: LiveData<Resource<List<Repo>>> = Transformations.switchMap(_query) { search ->
        if (search.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            //results即为repoRepository.search的返回值
            repoRepository.search(search)
        }
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState


    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == _query.value) {
            return
        }
        //重新进行搜索
        nextPageHandler.reset()
        _query.value = input

    }

    fun loadNextPage() {
        _query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(it)
            }
        }
    }

    fun refresh() {
        _query.value?.let { _query.value = it }
    }


    /**
     * 加载更多信息
     */
    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }


    class NextPageHandler(private val repoRepository: RepoRepository) :
        Observer<Resource<Boolean>> {

        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null

        val loadMoreState = MutableLiveData<LoadMoreState>()

        private var query: String? = null

        private var _hasMore: Boolean = false

        /**
         * 作用
         */
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        /***
         * 用于获取下一页数据
         */
        fun queryNextPage(query: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repoRepository.searchNextPage(query)
            loadMoreState.value = LoadMoreState(isRunning = true, errorMessage = null)

            nextPageLiveData?.observeForever(this)

        }


        override fun onChanged(t: Resource<Boolean>?) {
            if (t == null) {
                reset()
            } else {
                when (t.status) {
                    Status.SUCCESS -> {
                        _hasMore = t.data == true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )

                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = t.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        //取消Livedata的观察者
        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(isRunning = true, errorMessage = null)
        }
    }
}