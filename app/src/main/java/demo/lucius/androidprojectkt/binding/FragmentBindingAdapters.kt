package demo.lucius.androidprojectkt.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import javax.inject.Inject

/**
 * 在Fragment初始化时，绑定一个Adapter
 */
class FragmentBindingAdapters @Inject constructor(val fragment:Fragment){

    @BindingAdapter(value=["imageUrl","imageRequestListener"],requireAll = false)
    fun bindImage(imageView: ImageView,url:String?,listener:RequestListener<Drawable?>?){
        Glide.with(fragment).load(url).listener(listener).into(imageView)
    }
}