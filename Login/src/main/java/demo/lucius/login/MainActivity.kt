package demo.lucius.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.abunka.multipleappmodules.login.LoginNavigation
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

//    @Inject
//    lateinit var navigation: LoginNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
