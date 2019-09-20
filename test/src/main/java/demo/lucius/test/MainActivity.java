package demo.lucius.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import dagger.android.support.DaggerAppCompatActivity;

import javax.inject.Inject;

public class MainActivity extends DaggerAppCompatActivity {

    @Inject
    LoginNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
