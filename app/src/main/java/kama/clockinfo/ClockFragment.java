package kama.clockinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kat on 17/12/2017.
 */

public class ClockFragment extends Fragment {


    public static ClockFragment newInstance(){
        ClockFragment f = new ClockFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_clock, null);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("myApp", "clock frag created");

        View v = getActivity().findViewById(R.id.topFragmentFrame);

        //link member variables to layout items
        //there are none now

    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d("myApp", "clock frag started");
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resumed clock fragment");

        setTextViews();
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d("myApp", "paused clock frag");
    }

    public void setTextViews(){

        Log.d("myApp", "setting clock textviews");

        Date date = new Date();

       /* //set time
        DateFormat timeFormat = new SimpleDateFormat("HH:mm a");
        String currentTime = timeFormat.format(date);
        currentTime = "";
        mClock.setText(currentTime);*/


    }
}
