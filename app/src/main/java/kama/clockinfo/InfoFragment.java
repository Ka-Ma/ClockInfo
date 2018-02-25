package kama.clockinfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.FTPClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * Created by Kat on 17/12/2017.
 */

public class InfoFragment extends Fragment {

    TextView mInfo, mObsInfo;
    ImageView mObsImg;
    String locn = "94609";  //defaults to jandakot
    String mTime, mTemp, mCloud, mRainTrace, mHumidity, mWindDir, mWindSpd;
    Integer loop = 0;

    //handler & runnable for getting current observations
    Handler getInfoHandler = new Handler();
    Runnable getInfoRunnable = new Runnable() {
        @Override
        public void run(){

            new getInfo().execute();

            long ms = GetTimes.tilNext();

            getInfoHandler.postDelayed(this, ms);

        }
    };

    //handler & runnable for getting tomorrows precis
    Handler getForecastHandler = new Handler();
    Runnable getForecastRunnable = new Runnable() {
        @Override
        public void run(){

            new getTomorrow().execute();

            long ms = GetTimes.tilTomorrow();

            getForecastHandler.postDelayed(this, ms);

        }
    };

    //handler & runnable for displaying alternating information on screen
    Handler setInfoHandler = new Handler();
    Runnable setInfoRunnable = new Runnable(){
        @Override
        public void run(){
            Spannable msgSpn = new SpannableStringBuilder();
            mObsImg.setAdjustViewBounds(true);
            mObsImg.setMaxHeight(200);
            mObsImg.setMaxWidth(200);


            //TODO want to add icons to reduce footprint of info (need to adjust images to be 200px & the right colour)

            switch (loop){
                case 0: msgSpn = new SpannableStringBuilder(mTemp+(char) 0x00B0+"C");
                    mObsImg.setVisibility(View.GONE);
                    break;

                case 1: //TODO change this so the appropriate weather graphic shows

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
                        msgSpn = new SpannableStringBuilder(("no rainfall"));
                        setFontSizeForPath(msgSpn, "no rainfall", (int) mObsInfo.getTextSize() - 10);
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

            loop++;
            if(loop>3){loop=0;}

            setInfoHandler.postDelayed(this, 5000); //5 secs
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

        //Handlers for timed events
        getInfoHandler.postDelayed(getInfoRunnable, 0);
        getForecastHandler.postDelayed(getForecastRunnable, 0);
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
            //TODO getting tomorrows info needs to be finished http://commons.apache.org/proper/commons-net/javadocs/api-3.6/index.html
            Log.d("myApp", "getting tomorrows forecast");

            String xmlString = "";

            FTPClient ftpClient = new FTPClient();
            FileOutputStream stream = null;

            try{
                ftpClient.connect("ftp.bom.gov.au");

                //Log.d("myApp", "connected? " + ftpClient.getReplyString());
                Log.d("myApp", "connected? " + ftpClient.getReplyCode());

                //login
                Log.d("myApp", "it logged in " + ftpClient.login("Anonymous", "Guest"));

                Log.d("myApp", "logged in? " + ftpClient.getReplyString());
                //Log.d("myApp", "logged in? " + ftpClient.getReplyCode());

                //change directory
                Log.d("myApp", "it changed dir "+ ftpClient.changeWorkingDirectory("/anon/gen/fwo"));

                Log.d("myApp", "pwd is " + ftpClient.printWorkingDirectory() );
                Log.d("myApp", "list of files ");

                FTPFile[] rah = ftpClient.listFiles();

                Log.d("myApp", "there are "+rah.length+" files");
                /*
                for(int i = 0; i<rah.length; i++){
                    Log.d("myApp", rah[i].getName());
                }
                */

                //download file
                String filename = "IDW14199.xml";
                Log.d("myApp", "file is " + filename);

                File fileDir = getActivity().getFilesDir();

                File file = new File(fileDir, filename);
                Log.d("myApp", "setting writable "+file.setWritable(true));
                Log.d("myApp", "writable? "+file.canWrite());
                stream = new FileOutputStream(file);

                Log.d("myApp", "retrieving file");
                ftpClient.enterLocalPassiveMode();
               // Log.d("myApp", "status "+ftpClient.getStatus());
               // Log.d("myApp", "reply code "+ftpClient.getReplyCode());
                Log.d("myApp", "did it retrieve the file? "+ftpClient.retrieveFile(filename, stream));
               // Log.d("myApp", "reply code "+ftpClient.getReplyCode());
               // Log.d("myApp", "status "+ftpClient.getStatus());

                //Log.d("myApp", "the local file "+ file.toString());


                //TODO parse xml https://developer.android.com/training/basics/network-ops/xml.html
                //see dropbox bomSample.xml

                FileInputStream in = getActivity().openFileInput(filename);
                BOMXmlParser bomParser = new BOMXmlParser();
                List<BOMXmlParser.Forecast> forecasts = null;

                try {
                    forecasts = bomParser.parse(in);

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } finally {
                    if(in != null){
                        in.close();
                    }
                }

/*
                //test read of file
                FileInputStream inStream = getActivity().openFileInput(filename);
                InputStreamReader inputSR = new InputStreamReader(inStream);
                BufferedReader buffRead = new BufferedReader(inputSR);

                StringBuilder finalString = new StringBuilder();
                String oneline;

                while((oneline = buffRead.readLine())!=null){
                    //Log.d("myApp", oneline);
                    finalString.append(oneline);
                }

                buffRead.close();
                inStream.close();
                inputSR.close();

                Log.d("myApp", "the xml is: "+finalString.toString());
                //ends test read
*/
                //file.delete();
                stream.close();
                ftpClient.logout();
                ftpClient.disconnect();

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

            Log.d("myApp", "new info at " + mTime + ": Wind: " + mWindSpd + mWindDir + " rain "
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

    private java.lang.Void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "area");
        //TODO finish this.

        return null;
    }


}
