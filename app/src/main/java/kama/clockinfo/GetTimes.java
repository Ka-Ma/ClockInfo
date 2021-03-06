package kama.clockinfo;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kat on 7/01/2018.
 */

public class GetTimes {

    public static long tilNext(){
        long ms = 600000;

        //TODO find ms til next o'clock or half past. Needs testing... seems to need a buffer for that fact that humans sieems to be involved in the update

//        Log.d("myApp", "finding ms to next obs update");

        Date date = new Date();

        //get minutes past hour
        DateFormat timeFormat = new SimpleDateFormat("m");
        String currentMins = timeFormat.format(date);
        Integer mins = Integer.parseInt(currentMins);

//        Log.d("myApp", "string = "+currentMins+" integer = "+mins);

        //added 1000ms buffer
        //if minutes is equal to or greater than 30 find ms to next hour
        if(mins >= 30){
            ms = (60 - mins) * 60000 + 2000;
        }else if(mins<30){
            //if minutes is less than 30 find ms to next half hour
            ms = (30 - mins) * 60000 + 2000;
        }

//        Log.d("myApp", "There are " + ms + " milliseconds until the next obs update which is " + ms/60000+" minutes");

        return ms;
    }

    public static long tilTomorrow(){
        long ms;

        //TODO find ms til next day. needs testing

//        Log.d("myApp", "finding ms to next day ticking over");

        Date date = new Date();

        //get minutes and hours
        DateFormat hrFormat = new SimpleDateFormat("H");
        DateFormat minFormat = new SimpleDateFormat("m");
        String currentHr = hrFormat.format(date);
        String currentMin = minFormat.format(date);
        Integer hours = Integer.parseInt(currentHr);
        Integer mins = Integer.parseInt(currentMin);

//        Log.d("myApp", "got hours "+hours+" mins "+mins);

        //60 - minutes + 24 - hours * 60 * milliseconds
        ms = ((60 - mins) + ((24 - hours) * 60)) * 60000;

//        Log.d("myApp", "Calc is 60 - "+mins+" = "+ (60-mins)+" + 24 - "+hours+" = "
//                +(24-hours)+" * 60 = "+((24-hours)*60)+" * 60000 = " + ms
//                + " milliseconds until the next day");
//        Log.d("myApp", "minutes til tomorrow = "+ ms/60000);
//        Log.d("myApp", "hours til tomorrow = "+ ms/60000/60);

        return ms;
    }

    public static long tilGivenTime(Date givenTime){
        long ms, start, end;

        //TODO find ms til given date/time. needs testing

//        Log.d("myApp", "finding ms to given time");

        Date date = new Date();
        start = date.getTime();
        end = givenTime.getTime();
        ms = end-start;

//        Log.d("myApp", "start = "+start);
//        Log.d("myApp", "end = "+end);
//        Log.d("myApp", "diff = "+ms);

        return ms;
    }

    /**
     * returns a Date object given a string in the format of d/M/yyyy h:m a
     * eg: 10/03/2018 04:40 AM
     * **/
    public static Date setDateTime(String given){
        Date date = null;

        SimpleDateFormat ft = new SimpleDateFormat("d/M/yyyy h:m a");

        try{
            date = ft.parse(given);
        }catch (ParseException e){
            Log.e("myApp", "Can't parse this with " + ft);
        }

        return date;
    }

    /** returns string of tomorrows date**/
    public static String getTomorrow(){
        String date;
        Date today = new Date();
        Date tomorrow = new Date(today.getTime()+(24 * 60 * 60 * 1000));

        SimpleDateFormat ft = new SimpleDateFormat("d/M/yyyy");
        date = ft.format(tomorrow);

//        Log.d("myApp", "according to my calculations, tomorrow is " + date);

        return date;
    }

}
