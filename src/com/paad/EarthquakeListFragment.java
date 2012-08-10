package com.paad;

import android.*;
import android.R;
import android.app.ListFragment;
import android.location.Location;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import org.w3c.dom.Element;
import android.widget.ArrayAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: l344929
 * Date: 8/7/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class EarthquakeListFragment extends ListFragment{
    ArrayAdapter<Quake> arrayAdapter;
    ArrayList<Quake> earthquakes = new ArrayList<Quake>();

    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        int layoutID = R.layout.simple_list_item_1;
        arrayAdapter = new ArrayAdapter<Quake>(getActivity(), layoutID, earthquakes);
        setListAdapter(arrayAdapter);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthquakes();
            }
        });
        t.start();
    }


    public void refreshEarthquakes(){
        URL url;
        try{
            String quakeFeed = getString(com.paad.R.string.quake_feed);
            url = new URL(quakeFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream inputStream = httpConnection.getInputStream();

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document dom = documentBuilder.parse(inputStream);
                Element docEle = dom.getDocumentElement();

                earthquakes.clear();

                NodeList nl = docEle.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0 ; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("title").item(0);
                        Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
                        Element when = (Element)entry.getElementsByTagName("updated").item(0);
                        Element link = (Element)entry.getElementsByTagName("link").item(0);

                        String details = title.getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.gov";
                        String linkString = hostname + link.getAttribute("href");

                        String point = g.getFirstChild().getNodeValue();
                        String dt = when.getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                        Date qdate = new GregorianCalendar(0,0,0).getTime();
                        try {
                            qdate = sdf.parse(dt);
                        } catch (java.text.ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }

                        String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        l.setLatitude(Double.parseDouble(location[0]));
                        l.setLongitude(Double.parseDouble(location[1]));

                        String magnitudeString = details.split(" ")[1];
                        int end = magnitudeString.length()-1;
                        double magnitude = Double.parseDouble(magnitudeString.substring(0, end));

                        details = details.split(",")[1].trim();

                        final Quake quake = new Quake(qdate, details, l, magnitude, linkString);

                        // Process a newly found earthquake
                        handler.post(new Runnable() {
                            public void run() {
                                addNewQuake(quake);
                            }
                        });
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException");
        } catch (IOException e) {
            Log.d(TAG, "IOException");
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "Parser Configuration Exception");
        } catch (SAXException e) {
            Log.d(TAG, "SAX Exception");
        }
        finally {
        }
    }

    private void addNewQuake(Quake _quake){
        Earthquake earthquakeActivity = (Earthquake)getActivity();
        if(_quake.getMagnitude() > earthquakeActivity.minimumMagnitude){
        earthquakes.add(_quake);
        }

        arrayAdapter.notifyDataSetChanged();
    }
}
