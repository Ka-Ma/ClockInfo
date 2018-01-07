package kama.clockinfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Kat on 17/12/2017.
 */

public class InfoFragment extends Fragment {

    TextView mInfo, mObsInfo;
    String locn = "94609";  //defaults to jandakot
    String mTime, mTemp, mCloud, mRainTrace, mHumidity, mWindDir, mWindSpd;
    Integer loop = 0;

    Handler getInfoHandler = new Handler();
    Runnable getInfoRunnable = new Runnable() {
        @Override
        public void run(){

            new getInfo().execute();

            long ms = tilNext();

            getInfoHandler.postDelayed(this, ms);

        }
    };

    Handler setInfoHandler = new Handler();
    Runnable setInfoRunnable = new Runnable(){
        @Override
        public void run(){
            String msg;

            switch (loop){
                case 0: msg = mTemp+(char) 0x00B0+"C";
                    break;
                case 1: if(mCloud.contains("-")){msg = mRainTrace+"mm";}else{msg=mCloud+" "+mRainTrace+"mm";}
                    break;
                case 2: msg = mWindSpd+"kmh "+mWindDir;
                    break;
                case 3: msg = mHumidity+"% humidity";
                    break;
                default: msg= "an Error has occurred";
            }

            mInfo.setText(msg);
            mObsInfo.setText(" at "+mTime);
            loop++;
            if(loop>3){loop=0;}

            setInfoHandler.postDelayed(this, 10000); //10 secs
        }
    };


    public static InfoFragment newInstance(){
        InfoFragment f = new InfoFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_info, null);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("myApp", "info frag created");

        View v = getActivity().findViewById(R.id.middleFragmentFrame);

        //link member variables to layout items
        mInfo = v.findViewById(R.id.info);
        mObsInfo = v.findViewById(R.id.obsInfo);

        //Handlers for timed events
        getInfoHandler.postDelayed(getInfoRunnable, 0);
        setInfoHandler.postDelayed(setInfoRunnable, 10000);

    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d("myApp", "info frag started");

    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resumed info fragment");

    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d("myApp", "paused info frag");
    }

    private class getTomorrow extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params){

            Log.d("myApp", "getting tomorrows forecast");

            String xmlString = "";

            try{
                URL url = new URL("ftp://ftp.bom.gov.au/anon/gen/fwo/IDW14199.xml");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // gets the server json data
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;


                while ((line = bufferedReader.readLine()) != null) {
                    //Log.d("myApp", "concat the trimmed: " + line.trim());
                    xmlString = xmlString.concat(line.trim());
                    //Log.d("myApp", "xmlString " + xmlString);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class getInfo extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("myApp", "running in background");

            String jsonString = "";

            Log.d("myApp", "getting json");

            try {
                URL url = new URL("http://reg.bom.gov.au/fwo/IDW60901/IDW60901." + locn + ".json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // gets the server json data
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;


                while ((line = bufferedReader.readLine()) != null) {
                    //Log.d("myApp", "concat the trimmed: " + line.trim());
                    jsonString = jsonString.concat(line.trim());
                    //Log.d("myApp", "jsonString " + jsonString);
                }

                urlConnection.disconnect();

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.d("myApp", "the string " + jsonString);

            //put json string in an object/array to get info we want
            try {
                Log.d("myApp", "trying to parse json");
                JSONObject jandakot = new JSONObject(jsonString);
                Log.d("myApp", "jandakot object " + jandakot.toString());
                JSONObject jandakotObs = jandakot.getJSONObject("observations");
                Log.d("myApp", "jandakot obs object " + jandakotObs.toString());
                JSONArray jandakotData = jandakotObs.getJSONArray("data");
                Log.d("myApp", "jandakot data array " + jandakotData.toString());
                JSONObject jandakotLatest = jandakotData.getJSONObject(0);
                Log.d("myApp", "jandakot latest object " + jandakotLatest.toString());
                mTime = jandakotLatest.getString("local_date_time");
                mTemp = jandakotLatest.getString("air_temp");
                mCloud = jandakotLatest.getString("cloud");
                mHumidity = jandakotLatest.getString("rel_hum");
                mRainTrace = jandakotLatest.getString("rain_trace");
                mWindDir = jandakotLatest.getString("wind_dir");
                mWindSpd = jandakotLatest.getString("wind_spd_kmh");

            } catch (JSONException e) {
                Log.d("myApp", "There was an exception: " + e);
                Log.d("myApp", "Cause: " + e.getCause());
                e.printStackTrace();


            }

            //remove date leader from time
            int index = mTime.indexOf("/");
            mTime = mTime.substring(index + 1);

            Log.d("myApp", "new info at " + mTime + ": Wind: " + mWindSpd + mWindDir + " rain "
                    + mRainTrace + " cloud " + mCloud + " humidity " + mHumidity + " Temp " + mTemp);

            return null;

        }
    }

    private long tilNext(){
        long ms = 600000;

        //TODO find ms til next o'clock or half past.

        return ms;
    }
}
