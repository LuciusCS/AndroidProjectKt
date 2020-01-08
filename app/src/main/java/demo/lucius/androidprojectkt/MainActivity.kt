package demo.lucius.androidprojectkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import demo.lucius.androidprojectkt.util.autoCleared
import javax.inject.Inject
import kotlin.jvm.functions.FunctionN

class MainActivity : AppCompatActivity(),HasSupportFragmentInjector {


    var list by autoCleared<MutableList<String>>()

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun supportFragmentInjector()= dispatchingAndroidInjector
}
