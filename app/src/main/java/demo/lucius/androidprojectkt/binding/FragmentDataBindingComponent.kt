package demo.lucius.androidprojectkt.binding

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment


/**
 * 用于Fragment的databinding的组件
 */
class FragmentDataBindingComponent(fragment:Fragment): DataBindingComponent {

    private val adapter=FragmentBindingAdapters(fragment)


    override fun getFragmentBindingAdapters() = adapter


}