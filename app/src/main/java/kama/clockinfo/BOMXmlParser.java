package kama.clockinfo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kat on 25/02/2018.
 */

public class BOMXmlParser {
    private static final String ns = null; //namespace

    public List<Forecast> parse(InputStream in) throws XmlPullParserException, IOException {
        Log.d("myApp", "parsing XML");

        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Forecast> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("myApp", "reading feed");

        List<Forecast> forecasts = new ArrayList<Forecast>();

        parser.require(XmlPullParser.START_TAG, ns, "product");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("myApp", "found " + name);
            // Starts by looking for the area tag then the identifier for Perth
            if (name.equals("forecast")) {
                forecasts = (readArea(parser));
            } else {
                skip(parser);
            }
        }
        return forecasts;
    }

    //TODO need a readArea which then readForecast for the first two only
    private List<Forecast> readArea(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("myApp", "reading area");

        List<Forecast> forecasts = new ArrayList<Forecast>();

        parser.require(XmlPullParser.START_TAG, ns, "forecast");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("myApp", "name is " + name);
            // Starts by looking for the area tag then the identifier for Perth
            if (name.equals("area")) {//&& parser.getAttributeValue(ns, "acc").equals("WA_PT053")){
            //if ((name.equals("forecast-period") && (parser.getAttributeValue(ns, "index").equals("0") || parser.getAttributeValue(ns, "index").equals("1")))) {
                forecasts.add(readForecast(parser));
            } else {
                skip(parser);
            }
        }
        return forecasts;
    }

    //The Forecast details stored in this class
    public static class Forecast {
        public final String day;
        public final String min;
        public final String max;
        public final String precis;
        public final String rainChance;

        private Forecast(String day, String min, String max, String precis, String rainChance){
            this.day = day;
            this.min = min;
            this.max = max;
            this.precis = precis;
            this.rainChance = rainChance;
        }
    }

    //parse the content of forecast
    private Forecast readForecast(XmlPullParser parser) throws XmlPullParserException, IOException{
        Log.d("myApp", "reading forecast");

        parser.require(XmlPullParser.START_TAG, ns, "forecast-period");

        String day = null;
        String min = null;
        String max = null;
        String precis = null;
        String rainChance = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            //TODO need to consider how to get this information out of the BOM xml because the "names" are generic and the "type" is what actually identifies the element for some of the fields
            String name = parser.getAttributeValue(ns, "type");
           /* if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("summary")) {
                summary = readSummary(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }*/
        }
        return new Forecast(day, min, max, precis, rainChance);
    }

    //TODO add in the readTAG methods

    //skips tags parser not interested in
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("myApp", "skipping a tag");

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
