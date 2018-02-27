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
            String attrib = null;
            if (parser.getAttributeCount() != 0){
                attrib = parser.getAttributeName(0);
            }
            Log.d("myApp", "in readFeed: found " + name + " with attrib " + attrib);
            // Starts by looking for the area tag then the identifier for Perth
            if (name.equals("forecast")) {
                forecasts = (readForecast(parser));
            } else {
                skip(parser);
            }
        }
        return forecasts;
    }

    private List<Forecast> readForecast(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("myApp", "reading forecast");

        List<Forecast> forecasts = new ArrayList<Forecast>();

        parser.require(XmlPullParser.START_TAG, ns, "forecast");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String attrib = null;
            String attribVal = null;
            if (parser.getAttributeCount() != 0){
                attrib = parser.getAttributeName(0);
                attribVal = parser.getAttributeValue(ns, attrib);
            }
            Log.d("myApp", "in readForecast: found " + name + " with attrib " + attrib + " of value " + attribVal);
            // Starts by looking for the area tag then the identifier for Perth
            if (name.equals("area") && parser.getAttributeValue(ns, "aac").equals("WA_PT053")){
                forecasts = readArea(parser);
            } else {
                skip(parser);
            }
        }
        return forecasts;
    }

    private List<Forecast> readArea(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("myApp", "reading area");

        List<Forecast> forecasts = new ArrayList<Forecast>();

        parser.require(XmlPullParser.START_TAG, ns, "area");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String attrib = null;
            String attribVal = null;
            if (parser.getAttributeCount() != 0){
                attrib = parser.getAttributeName(0);
                attribVal = parser.getAttributeValue(ns, attrib);
            }

            Log.d("myApp", "in readArea: found " + name + " with attrib " + attrib + " of value " + attribVal);

            String day = null;
            if (attrib.equals("index") && attribVal.equals("0")) {
                day = "today";
            } else if (attrib.equals("index") && attribVal.equals("1")) {
                day = "tomorrow";
            } else {
                skip(parser);
            }

            if (day != null){
                forecasts.add(readPeriod(parser, day));
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
        //TODO add code?

        private Forecast(String day, String min, String max, String precis, String rainChance){
            this.day = day;
            this.min = min;
            this.max = max;
            this.precis = precis;
            this.rainChance = rainChance;
        }
    }

    //parse the content of forecast
    private Forecast readPeriod(XmlPullParser parser, String d) throws XmlPullParserException, IOException{
        Log.d("myApp", "reading period");

        parser.require(XmlPullParser.START_TAG, ns, "forecast-period");

        String day = d;
        String min = null;
        String max = null;
        String precis = null;
        String rainChance = null;
        String code = null;

        while (parser.next() != XmlPullParser.END_TAG) { //3
            if (parser.getEventType() != XmlPullParser.START_TAG) { //2
                continue;
            }
            //TODO need to consider how to get this information out of the BOM xml because the "names" are generic and the "type" is what actually identifies the element for some of the fields
            String name = parser.getName();
            String attrib = null;
            String attribVal = null;
            String value = null;

            if (parser.getAttributeCount() != 0) {
                attrib = parser.getAttributeName(0);
                attribVal = parser.getAttributeValue(ns, attrib);
            }

            value = parser.getText();

            Log.d("myApp", "in readPeriod: found name(" + name + ") with attrib(" + attrib + ") of value (" + attribVal + ") with a tag value(" + value + ")");

            if (attrib.equals("index") && attribVal.equals("0")) {
                day = "today";
            } else if (attrib.equals("index") && attribVal.equals("1")) {
                day = "tomorrow";
            } else if ((attrib.equals("index")) && (!attribVal.equals("0") || !attribVal.equals("1"))) {
                Log.d("myApp", "attrib is " + attrib + " (should be index) = " + attribVal + " which is not 0 or 1");
                skip(parser);
            } else if (attribVal.equals("precis")) {
                precis = readPrecisPrecip(parser);
            } else if (attribVal.equals("probability_of_precipitation")) {
                rainChance = readPrecisPrecip(parser);
            } else if (attribVal.equals("air_temperature_minimum")) {
                min = readMinMaxCode(parser);
            } else if (attribVal.equals("air_temperature_maximum")) {
                max = readMinMaxCode(parser);
            } else if (attribVal.equals("forecast_icon_code")) {
                code = readMinMaxCode(parser);
            } else {
                Log.d("myApp", "in readPeriod: didn't find listed tag, found " + name + " " + attrib + "  " + attribVal);
                skip(parser);
            }

            //reset
            name = attrib = attribVal = value = null;
        } // end while

        return new Forecast(day, min, max, precis, rainChance);
    }

    //TODO add in the readTAG methods, check these
    private String readPrecisPrecip(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "text");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "text");
        return result;
    }

    private String readMinMaxCode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "element");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "element");
        return result;
    }

    private String readElement(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.COMMENT ) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

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
//                    Log.d("myApp", "skipping: found end tag, current depth " + depth + " name " + parser.getName());
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
//                    Log.d("myApp", "skipping: found start tag, current depth " + depth +  " name " + parser.getName());
                    break;
            }
        }
    }
}
