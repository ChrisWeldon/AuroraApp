package org.evanscmssm.auroranotifyer;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by chrisevans on 9/17/16.
 */
public class CoordinatesHandler  {
    Integer[][] CoordinatesValue = new Integer[1024][512];
    private final double LON_INC = 0.3284671;
    private final double LAT_INC = 0.3515625;






    public void update(BufferedReader reader){
        String line;
        int y=0;

        while(true){

            try {
                line = reader.readLine();

                if(line == null){
                    break;
                }
                if (!line.contains("#")) {
                    line = line.trim();
                    String splitCords[] = line.split("\\s+");
                    for (int x = 0; x < 1024;x++) {
                        try {
                            CoordinatesValue[x][y] = Integer.parseInt(splitCords[x]);
                        }catch(NumberFormatException e){
                            Log.d(x+" and "+y, "'"+CoordinatesValue[x][y]+"'");
                        }
                        //Log.d(Integer.toString(x),Integer.toString(y));

                    }
                    y++;
                }

            }catch(IOException e){Log.d("error", "IO Exception");}
        }
    }

    public static void download(Context context, String url, Downloader.DownloadHandler<CoordinatesHandler> handler){
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("Network", "I can talk to the world");

            Downloader<CoordinatesHandler> downloader = new Downloader<CoordinatesHandler>(new Downloader.StreamHandler<CoordinatesHandler>() {
                @Override
                public CoordinatesHandler handleStream(InputStream strm) {

                    CoordinatesHandler data = new CoordinatesHandler();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(strm, "UTF-8"));
                        data.update(reader);
                        Log.d("Log", "Buffered Reader Created");
                    } catch (UnsupportedEncodingException e) {
                        Log.d("error", "Unsupported Encoding Exception");
                    }
                    return data;
                }
            }, handler);

            downloader.start(context, url);

            {
                handler.onDownloadError(1, "Unable to connect to network");
            }
        }
    }



    public Integer getProbability(Location l){

        double lat = l.getLatitude();
        double lon = l.getLongitude();
        int latIndex;
        int lonIndex;
        //spoofing latatude and longitude for Fairbanks Alaska
        lat = 64.842217;
        lon =  -147.753461;
        //should end up being lat:439 , lon:91

        Log.d("lat", Double.toString(lat));
        Log.d("lon", Double.toString(lon));

        lat = lat + 90;
        if(lat<0){
            lat = 0;
        }
        //0 is row 0, and 180 is row 511

        lon = lon +180;

        if(lon<0){
            lon = 0;
        }
        if(lon>360){
            lon = 360;
        }



        Double t = new Double(511*lat / 180);
        Double n = new Double(1023*lon/360);
        latIndex = t.intValue();
        lonIndex = n.intValue();

        // TODO make probablity a blur grid so its more accurate for the field of view

        /*determining what the index is to get the probability*/

        Log.d("Log","Got probability");
        String probability = Integer.toString(CoordinatesValue[latIndex][lonIndex]);
        Log.d("Probability",probability );



        return CoordinatesValue[latIndex][lonIndex];

    }


}
