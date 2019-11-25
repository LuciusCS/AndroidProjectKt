package demo.lucius.androidprojectkt.ui.common

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.adapters.ViewBindingAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import demo.lucius.androidprojectkt.AppExecutors


/**
 * 通用的RecyclerView适配器，使用Databinding和DiffUtil
 */
abstract class DataBoundListAdapter<T, V : ViewDataBinding>(
    appExecutors: AppExecutors,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundViewHolder<V>>(
    AsyncDifferConfig.Builder<T>(diffCallback).setBackgroundThreadExecutor(
        appExecutors.diskIO()
    ).build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
         val binding=createBinding(parent)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup):V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        bind(holder.binding,getItem(position))
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding:V,item:T)

}