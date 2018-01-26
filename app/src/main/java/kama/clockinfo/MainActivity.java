package kama.clockinfo;

import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton mHelpBtn;
    ImageButton mSettingsBtn;

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

        View v = findViewById(R.id.mainView);

        mHelpBtn = v.findViewById(R.id.helpBtn);
        mSettingsBtn = v.findViewById(R.id.settingsBtn);

        if (savedInstanceState == null) {
            ClockFragment clockFragment = ClockFragment.newInstance();
            InfoFragment infoFragment = InfoFragment.newInstance();
            DateFragment dateFragment = DateFragment.newInstance();
            //HelpFragment helpFragment = HelpFragment.newInstance();
            //SettingsFragment settingsFragment = SettingsFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.topFragmentFrame, clockFragment, "Top");
            ft.add(R.id.middleFragmentFrame, infoFragment, "middle");
            ft.add(R.id.bottomFragmentFrame, dateFragment, "bottom");
            //ft.add(R.id.helpFragmentFrame, helpFragment, "help");
            //ft.add(R.id.settingsFragmentFrame, helpFragment, "settings");
            ft.commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resuming main activity");

        mHelpBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //TODO add action to help button
                //another fragment/activity within which will document relevent stuff,
                //eg: the fact that the current observations are sometimes posted quite late.

                Toast.makeText(getApplicationContext(), "this is the help button", Toast.LENGTH_SHORT).show();
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //TODO add action to settings button
                //another fragment/activity within which you can select from a list of available
                //areas for observations and forecast

                Toast.makeText(getApplicationContext(), "this is the settings button", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
