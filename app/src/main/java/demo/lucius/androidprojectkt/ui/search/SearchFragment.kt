package demo.lucius.androidprojectkt.ui.search

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import demo.lucius.androidprojectkt.AppExecutors
import demo.lucius.androidprojectkt.R
import demo.lucius.androidprojectkt.binding.FragmentDataBindingComponent
import demo.lucius.androidprojectkt.databinding.SearchFragmentBinding
import demo.lucius.androidprojectkt.di.Injectable
import demo.lucius.androidprojectkt.ui.common.RepoListAdapter
import demo.lucius.androidprojectkt.ui.common.RetryCallback
import demo.lucius.androidprojectkt.util.autoCleared
import javax.inject.Inject

class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

//    @Inject



    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    /**
     * by 关键字用来简化实现代理 (委托) 模式，不仅可以类代理，还可以代理类属性, 监听属性变化，
     * 使用委托模式binding
     */
    var binding by autoCleared<SearchFragmentBinding>()

    var adapter by autoCleared<RepoListAdapter>()



    val searchViewModel: SearchViewModel by viewModels {
        viewModelFactory
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.search_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.setLifecycleOwner(this)
//        binding.setLifecycleOwner(viewLifecycleOwner)
        initRecyclerView()

        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            showFullName = true
        ) {
            //用于跳转到特定的Repo
        }

        binding.query = searchViewModel.query
        binding.repoList.adapter = rvAdapter
        adapter = rvAdapter

        initSearchInputListener()


        binding.callback=object :RetryCallback{
            //retry()在布局文件中会被调用
            override fun retry() {
                searchViewModel.refresh()
            }
        }
    }

    //用于初始化输入框
    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }

        binding.input.setOnKeyListener { view: View,
                                         keyCode: Int, event: KeyEvent ->
           if (event.action==KeyEvent.ACTION_DOWN&&keyCode==KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
               true
            }else{
               false
           }
        }
    }

    //用于进行搜索
    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        dismissKeyboard(v.windowToken)
        searchViewModel.setQuery(query)
    }

    //    用于初始化RecyclerView
    private fun initRecyclerView() {
        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })
        binding.searchResult = searchViewModel.results
        searchViewModel.results.observe(
            viewLifecycleOwner,
            Observer { result -> adapter.submitList(result?.data) })

        searchViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }

            }
        })

    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    fun navController() = findNavController()


}