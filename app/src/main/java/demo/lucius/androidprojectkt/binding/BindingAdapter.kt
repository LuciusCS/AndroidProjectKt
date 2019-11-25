package demo.lucius.androidprojectkt.binding

import android.view.View
import androidx.databinding.BindingAdapter


/**
 * 用于Databinding中绑定特定的属性
 */
object BindingAdapter {

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show:Boolean){
        view.visibility=if (show)View.VISIBLE else View.GONE
    }
}