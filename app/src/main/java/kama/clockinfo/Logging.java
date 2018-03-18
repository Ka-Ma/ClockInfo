package kama.clockinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kat on 13/03/2018.
 */

public class Logging {

    //send info to Log.d & to text file on device.

    public static void toFile(Context context, String msg){

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("y/MM/dd H:mm  ");
        DateFormat fileFormat = new SimpleDateFormat("yMMdd");
        String now = dateFormat.format(date);
        String fileNow = fileFormat.format(date);
        String filename = "clockInfo_LOG_"+fileNow+".txt";
        File logFile = null;

        //open file
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

            //File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "clockInfoLogging");
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "clockInfoLogging");
            if(!path.mkdirs()){
                Log.d("myApp", "opening log file didn't work");
            }

            logFile = new File(path, filename);
            Log.d("myApp", "logging to " + logFile.getPath());
            Log.d("myApp", "writable? " + logFile.canWrite());
            Log.d("myApp", "readable? " + logFile.canRead());
            Log.d("myApp", logFile.toString());

            //append message to file with time stamp
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(now);
                buf.append(msg);
                buf.newLine();
                buf.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        //force refresh on device so pc can see file
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(logFile)));

        //send message to console
        Log.d("myApp", "via logging " + msg);
    }

    public static void sendLogToConsole(){
        //get list of logs from clockInfoLogging directory

        //read them line by line to console, go!

    }
}
