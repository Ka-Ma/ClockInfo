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

public class DateFragment extends Fragment {

    TextView mDay;
    TextView mDate;

    public static DateFragment newInstance(){
        DateFragment f = new DateFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_date, null);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("myApp", "date frag created");

        View v = getActivity().findViewById(R.id.bottomFragmentFrame);

        //link member variables to layout items
        mDay = v.findViewById(R.id.day);
        mDate = v.findViewById(R.id.date);

    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d("myApp", "date frag started");
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resumed date fragment");

        setTextViews();
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d("myApp", "paused date frag");
    }

    public void setTextViews(){

        Log.d("myApp", "setting date textviews");

        Date date = new Date();

        //set date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(date);
        mDate.setText(currentDate);

        //set day
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        String currentDay = dayFormat.format(date);
        mDay.setText(currentDay);
    }
}
