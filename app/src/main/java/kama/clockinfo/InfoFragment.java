package kama.clockinfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.xmlpull.v1.XmlPullParserException;


/**
 * Created by Kat on 17/12/2017.
 */

public class InfoFragment extends Fragment {

    TextView mInfo, mObsInfo, mTomInfo, mTomObsInfo;
    ImageView mObsImg, mTomImg;
    String locn = "94609";  //defaults to jandakot for observations
    String mTime, mTemp, mCloud, mRainTrace, mHumidity, mWindDir, mWindSpd;
    Integer loopI = 0;
    Integer loopF = 0;
    Integer updateSpeed = 5000; //defaults to 5 seconds, ideally slower at night
    List<BOMXmlParser.Forecast> forecasts = null;


    //handler & runnable for getting current observations
    Handler getInfoHandler = new Handler();
    Runnable getInfoRunnable = new Runnable() {
        @Override
        public void run(){

            new getInfo().execute();

            //TODO this schedule seems unreliable, if the time is not close to the scheduled time it should check more frequently until it is updated.

            long ms = GetTimes.tilNext();

            getInfoHandler.postDelayed(this, ms);

        }
    };

    //handler & runnable for getting tomorrows precis
    Handler getForecastHandler = new Handler();
    Runnable getForecastRunnable = new Runnable() {
        @Override
        public void run(){
            //reset forecasts so display works as expected
            forecasts = null;

            new getTomorrow().execute();

            //TODO this information is updated on a fairly regular schedule, need to work out the best way to deal with that (4:40am, 10:32am, 4:20pm)
            // convert to 24 hr time?
            // if current time > 4:40am && < 10:32am, set ms for 10:32am
            // else if current time < 4:20pm, set ms for 4:20pm
            // else set ms for 4:40am tomorrow

            long ms = GetTimes.tilGivenTime(GetTimes.setDateTime(GetTimes.getTomorrow() +" 04:50 AM")); //4:40am plus a few otherwise have been missing it

//            Log.d("myApp", "there are " + ms + " milliseconds until tomorrow 4:40am");
//            Log.d("myApp", "there are " + (ms/1000/60/60) + " hours until tomorrow 4:40am" );

            getForecastHandler.postDelayed(this, ms);
        }
    };

    //handler & runnable for displaying tomorrows precis
    Handler setForecastHandler = new Handler();
    Runnable setForecastRunnable = new Runnable() {
        @Override
        public void run(){

            if(forecasts == null){
                Log.d("myApp", "forecasts is null, wait a bit");
                setForecastHandler.postDelayed(this, 10000);
            }else{
         //       Log.d("myApp", "forecasts is not null, can display");

                mTomObsInfo.setText(forecasts.get(1).day + " ("+forecasts.get(1).date+")");

                String msg = "";
                mTomInfo.setVisibility(View.VISIBLE);
                mTomImg.setVisibility(View.INVISIBLE);
                mTomImg.setMaxHeight(180);
                mTomImg.setMaxWidth(180);
                mTomImg.setImageResource(0);

                switch (loopF){
                    case 0:
                        msg = forecasts.get(1).min + "/" + forecasts.get(1).max;
                        break;

                    case 1:
                        mTomImg.setVisibility(View.VISIBLE);

                        switch(forecasts.get(1).code){
                            case "1":
                                mTomImg.setImageResource(R.drawable.sunny);
                                break;
                            case "2":
                                mTomImg.setImageResource(R.drawable.clear);
                                break;
                            case "3":
                                mTomImg.setImageResource(R.drawable.partly_cloudy);
                                break;
                            case "4":
                                mTomImg.setImageResource(R.drawable.cloudy);
                                break;
                            case "6":
                                mTomImg.setImageResource(R.drawable.haze);
                                break;
                            case "8":
                                mTomImg.setImageResource(R.drawable.light_rain);
                                break;
                            case "9":
                                mTomImg.setImageResource(R.drawable.wind);
                                break;
                            case "10":
                                mTomImg.setImageResource(R.drawable.fog);
                                break;
                            case "11":
                                mTomImg.setImageResource(R.drawable.showers);
                                break;
                            case "12":
                                mTomImg.setImageResource(R.drawable.rain);
                                break;
                            case "13":
                                mTomImg.setImageResource(R.drawable.dust);
                                break;
                            case "14":
                                mTomImg.setImageResource(R.drawable.frost);
                                break;
                            case "15":
                                mTomImg.setImageResource(R.drawable.snow);
                                break;
                            case "16":
                                mTomImg.setImageResource(R.drawable.storm);
                                break;
                            case "17":
                                mTomImg.setImageResource(R.drawable.light_showers);
                                break;
                            case "18":
                                mTomImg.setImageResource(R.drawable.heavy_showers);
                                break;
                            case "19":
                                mTomImg.setImageResource(R.drawable.tropicalcyclone);
                                break;
                            default:
                                mTomImg.setVisibility(View.GONE);
                                msg = forecasts.get(1).precis + " ";
                        }
                        mTomInfo.setVisibility(View.INVISIBLE);
                        break;

                    case 2:
                        mTomImg.setVisibility(View.VISIBLE);
                        mTomImg.setImageResource(R.drawable.rain);
                        msg = forecasts.get(1).rainChance;
                        break;

                    default: msg = "an Error has occurred";
                }

                loopF++;
                if(loopF>2){loopF=0;}

                mTomInfo.setText(msg);

                setForecastHandler.postDelayed(this, updateSpeed);  //TODO make this dependent on time of day (default update display every 5 secs)
            }
        }
    };

    //handler & runnable for displaying alternating information on screen
    Handler setInfoHandler = new Handler();
    Runnable setInfoRunnable = new Runnable(){
        @Override
        public void run(){
            Spannable msgSpn = new SpannableStringBuilder();
            mObsImg.setAdjustViewBounds(true);
            mObsImg.setMaxHeight(180);
            mObsImg.setMaxWidth(180);

            switch (loopI){
                case 0:
                    if(forecasts == null || forecasts.get(0).max == null){
                        msgSpn = new SpannableStringBuilder(mTemp + (char) 0x00B0 + "C");
                    }else{
                        msgSpn = new SpannableStringBuilder(mTemp + (char) 0x00B0 + "C/" + forecasts.get(0).max + (char) 0x00B0+"C");
                        setFontSizeForPath(msgSpn, forecasts.get(0).max + (char) 0x00B0+"C", (int) mObsInfo.getTextSize() - 10);
                    }
                    mObsImg.setVisibility(View.GONE);
                    break;

                case 1: //TODO change this so the appropriate weather graphic shows, there are time appropriate ones

                    mObsImg.setVisibility(View.VISIBLE);

                    switch(mCloud){
                        case "-": mObsImg.setVisibility(View.GONE);
                            break;
                        case "Clear": mObsImg.setImageResource(R.drawable.clear);
                            break;
                        case "Cloudy": mObsImg.setImageResource(R.drawable.cloudy);
                            break;
                        case "Partly cloudy":mObsImg.setImageResource(R.drawable.partly_cloudy);
                            break;
                        case "Mostly cloudy":mObsImg.setImageResource(R.drawable.cloudy);
                            break;
                        default: msgSpn = new SpannableStringBuilder(mCloud + " ");
                    }

                    if(!mRainTrace.equals("0.0")){
                        msgSpn = new SpannableStringBuilder(mRainTrace+"mm");
                    }

                    if((mCloud.equals("-"))&&(mRainTrace.equals("0.0"))){
                        mObsImg.setVisibility(View.VISIBLE);
                        mObsImg.setImageResource(R.drawable.no_rain);
                        //msgSpn = new SpannableStringBuilder(("No Rain"));
                        //setFontSizeForPath(msgSpn, "No Rain", (int) mObsInfo.getTextSize() - 5);
                    }

                    break;

                case 2: mObsImg.setVisibility(View.VISIBLE);

                    int dir = 0;

                    switch (mWindDir){
                        case "N": dir = R.drawable.wind_dir_n;
                            break;
                        case "NNE": dir = R.drawable.wind_dir_nne;
                            break;
                        case "NE": dir = R.drawable.wind_dir_ne;
                            break;
                        case "ENE": dir = R.drawable.wind_dir_ene;
                            break;
                        case "E": dir = R.drawable.wind_dir_e;
                            break;
                        case "ESE": dir = R.drawable.wind_dir_ese;
                            break;
                        case "SE": dir = R.drawable.wind_dir_se;
                            break;
                        case "SSE": dir = R.drawable.wind_dir_sse;
                            break;
                        case "S": dir = R.drawable.wind_dir_s;
                            break;
                        case "SSW": dir = R.drawable.wind_dir_ssw;
                            break;
                        case "SW": dir = R.drawable.wind_dir_sw;
                            break;
                        case "WSW": dir = R.drawable.wind_dir_wsw;
                            break;
                        case "W": dir = R.drawable.wind_dir_w;
                            break;
                        case "WNW": dir = R.drawable.wind_dir_wnw;
                            break;
                        case "NW": dir = R.drawable.wind_dir_nw;
                            break;
                        case "NNW": dir = R.drawable.wind_dir_nnw;
                            break;
                }

                    mObsImg.setImageResource(dir);
                    msgSpn = new SpannableStringBuilder(mWindSpd + " km/h");
                    setFontSizeForPath(msgSpn, " km/h", (int) mObsInfo.getTextSize() - 20);

                    break;

                case 3: msgSpn = new SpannableStringBuilder(mHumidity);
                    mObsImg.setVisibility(View.VISIBLE);
                    mObsImg.setImageResource(R.drawable.icon_humidity2);
                    break;

                default: msgSpn = new SpannableStringBuilder("an Error has occurred");
            }

            mInfo.setText(msgSpn);
            mObsInfo.setText(" at "+mTime);

            loopI++;
            if(loopI>3){loopI=0;}

            setInfoHandler.postDelayed(this, updateSpeed); 
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
        mObsImg = v.findViewById(R.id.infoImage);
        mTomInfo = v.findViewById(R.id.tomorrowInfo);
        mTomObsInfo = v.findViewById(R.id.tomorrowObsInfo);
        mTomImg = v.findViewById(R.id.tomorrowInfoImage);

        //Handlers for timed events
        getInfoHandler.postDelayed(getInfoRunnable, 0);
        getForecastHandler.postDelayed(getForecastRunnable, 0);
        setInfoHandler.postDelayed(setInfoRunnable, 10000);
        setForecastHandler.postDelayed(setForecastRunnable, 10000);

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
            Logging.toFile(getActivity().getApplicationContext(), "getting tomorrows forecast");

            FTPClient ftpClient = new FTPClient();
            FileOutputStream stream = null;
            //String filename = "IDW14199.xml";  //Precis Forecast XML Package (WA)
            String filename = "IDW12300.xml";	//City Forecast - Perth (WA)

            try{
                ftpClient.connect("ftp.bom.gov.au");

                //login
                Log.d("myApp", "it logged in " + ftpClient.login("Anonymous", "Guest"));

                //change directory
                Log.d("myApp", "it changed dir "+ ftpClient.changeWorkingDirectory("/anon/gen/fwo"));

                //download the file to be parsed
                File fileDir = getActivity().getFilesDir();
                File file = new File(fileDir, filename);
                file.setWritable(true);
                stream = new FileOutputStream(file);
                ftpClient.enterLocalPassiveMode();
                Log.d("myApp", "retrieved file? " + ftpClient.retrieveFile(filename, stream));

                //end ftp
                Log.d("myApp", "logged out? " + ftpClient.logout());
                ftpClient.disconnect();

                //parse XML
                FileInputStream in = getActivity().openFileInput(filename);
                BOMXmlParser bomParser = new BOMXmlParser();

                try {
                    forecasts = bomParser.parse(in);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } finally {
                    if(in != null){
                        in.close();
                        Log.d("myApp", "parsing over");
                    }
                }



            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                Log.e("myApp", "boom " + e1);
            } catch (SocketException e1) {
                e1.printStackTrace();
                Log.e("myApp", "boom " + e1);
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e("myApp", "boom " + e1);
            }

            for(int i = 0; i<forecasts.size();i++) {
                Logging.toFile(getActivity().getApplicationContext(), forecasts.get(i).day + ": code " + forecasts.get(i).code + " precis " + forecasts.get(i).precis);
            }

            return null;
        }
    }

    private class getInfo extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("myApp", "running in background");

            String jsonString = "";

            Logging.toFile(getActivity().getApplicationContext(), "getting json for current observations");

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


            //Log.d("myApp", "the string " + jsonString);

            //put json string in an object/array to get info we want
            try {
                Log.d("myApp", "trying to parse json");
                JSONObject jandakot = new JSONObject(jsonString);
                //Log.d("myApp", "jandakot object " + jandakot.toString());
                JSONObject jandakotObs = jandakot.getJSONObject("observations");
                //Log.d("myApp", "jandakot obs object " + jandakotObs.toString());
                JSONArray jandakotData = jandakotObs.getJSONArray("data");
                //Log.d("myApp", "jandakot data array " + jandakotData.toString());
                JSONObject jandakotLatest = jandakotData.getJSONObject(0);
                //Log.d("myApp", "jandakot latest object " + jandakotLatest.toString());
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

            Logging.toFile(getActivity().getApplicationContext(), "Time: " + mTime + " Wind: " + mWindSpd + mWindDir + " rain "
                    + mRainTrace + " cloud " + mCloud + " humidity " + mHumidity + " Temp " + mTemp);

            return null;

        }
    }

    public static void setFontSizeForPath(Spannable spannable, String path, int fontSizeInPixel) {
        //source: https://stackoverflow.com/questions/16335178/different-font-size-of-strings-in-the-same-textview
        int startIndexOfPath = spannable.toString().indexOf(path);
        spannable.setSpan(new AbsoluteSizeSpan(fontSizeInPixel), startIndexOfPath,
                startIndexOfPath + path.length(), 0);
    }



}