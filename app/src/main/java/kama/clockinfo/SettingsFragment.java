package kama.clockinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Kat on 24/1/2018.
 */

public class SettingsFragment extends Fragment {
    ImageButton mSettingsButton;


    public static SettingsFragment newInstance(){
        SettingsFragment f = new SettingsFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_settings, null);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("myApp", "settings frag created");

        View v = getActivity().findViewById(R.id.settingsFragmentFrame);

        //link member variables to layout items
        mSettingsButton = v.findViewById(R.id.settingsBtn);

    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d("myApp", "settings frag started");

        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //TODO add action to settings button
                //another fragment within which you can select from a list of available areas for observations and forecast
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resumed settings fragment");

    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d("myApp", "paused settings frag");
    }


}
