package kama.clockinfo;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kat on 7/01/2018.
 */

public class GetTimes {

    public static long tilNext(){
        long ms = 600000;

        //TODO find ms til next o'clock or half past. Needs testing

        Log.d("myApp", "finding ms to next obs update");

        Date date = new Date();

        //get minutes past hour
        DateFormat timeFormat = new SimpleDateFormat("m");
        String currentMins = timeFormat.format(date);
        Integer mins = Integer.parseInt(currentMins);

        Log.d("myApp", "string = "+currentMins+" integer = "+mins);

        //if minutes is equal to or greater than 30 find ms to next hour
        if(mins >= 30){
            ms = (60 - mins) * 100000;
        }else if(mins<30){
            //if minutes is less than 30 find ms to next half hour
            ms = (30 - mins) * 100000;
        }

        Log.d("myApp", "There are " + ms + " milliseconds until the next obs update");

        return ms;
    }

    public static long tilTomorrow(){
        long ms = 600000;

        //TODO find ms til next day

        Log.d("myApp", "finding ms to next day ticking over");

        Date date = new Date();

        //get minutes past hour
        DateFormat hrFormat = new SimpleDateFormat("h");
        DateFormat minFormat = new SimpleDateFormat("m");
        String currentHr = hrFormat.format(date);
        String currentMin = minFormat.format(date);
        Integer hours = Integer.parseInt(currentHr);
        Integer mins = Integer.parseInt(currentMin);

        Log.d("myApp", "got hours "+hours+" mins "+mins);

        //60 - minutes + 24 - hours * 60 * milliseconds
        ms = ((60 - mins) + ((24 - hours) * 60)) * 60000;

        Log.d("myApp", "There are " + ms + " milliseconds until the next day");

        return ms;
    }
}
