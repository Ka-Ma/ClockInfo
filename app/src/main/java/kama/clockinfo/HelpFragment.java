package kama.clockinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Date;

/**
 * Created by Kat on 24/1/2018.
 */

public class HelpFragment extends Fragment {

    public static HelpFragment newInstance(){
        HelpFragment f = new HelpFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_help, null);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("myApp", "help frag created");

        View v = getActivity().findViewById(R.id.helpFragmentFrame);

        //link member variables to layout items
        //TODO add help information
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d("myApp", "help frag started");


    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resumed help fragment");

    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d("myApp", "paused help frag");
    }


}
