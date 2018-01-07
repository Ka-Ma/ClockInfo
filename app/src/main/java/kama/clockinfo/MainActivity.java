package kama.clockinfo;

import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();

        //Handler to refresh the day and date textviews on the new day.

        if (savedInstanceState == null) {
            ClockFragment clockFragment = ClockFragment.newInstance();
            InfoFragment infoFragment = InfoFragment.newInstance();
            DateFragment dateFragment = DateFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.topFragmentFrame, clockFragment, "Top");
            ft.add(R.id.middleFragmentFrame, infoFragment, "middle");
            ft.add(R.id.bottomFragmentFrame, dateFragment, "bottom");
            ft.commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resuming main activity");
    }

}
