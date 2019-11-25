package demo.lucius.androidprojectkt.ui.common

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


/**
 * out 将泛型作为内部方法的返回
 * in 将泛型作为函数的参数
 */
class DataBoundViewHolder<out T : ViewDataBinding> constructor(val binding: T) :
    RecyclerView.ViewHolder(binding.root) {
}