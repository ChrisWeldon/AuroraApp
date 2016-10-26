package org.evanscmssm.auroranotifyer;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

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
    double lonInc = 2.844;
    double latInc = 2.844;




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

        if (networkInfo != null && networkInfo.isConnected())
        {
            Log.d("Network", "I can talk to the world");

            Downloader<CoordinatesHandler> downloader = new Downloader<CoordinatesHandler>(new Downloader.StreamHandler<CoordinatesHandler>() {
                @Override
                public CoordinatesHandler handleStream(InputStream strm) {
                    CoordinatesHandler data = new CoordinatesHandler();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(strm, "UTF-8"));
                        data.update(reader);
                    }catch(UnsupportedEncodingException e){}
                    return data;
                }
            }, handler);

            downloader.start(url);
        }
        else
        {
            handler.onDownloadError(1, "Unable to connect to network");
        }
    }



    public Integer getProbability(Location l){

        double lat = l.getLatitude();
        double lon = l.getLongitude();

        int latProb = (int)Math.round(lat*latInc);
        int lonProb = (int)Math.round(lon*lonInc);
        Log.d("latProb", Integer.toString(latProb));
        Log.d("lonProb", Integer.toString(lonProb));


        /*
        add the part that crosses location with fake data
         */

        return 1;

    }


}
