package org.evanscmssm.auroranotifyer;

import android.location.Location;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by chrisevans on 9/17/16.
 */
public class CoordinatesHandler{
    Integer[][] CoordinatesValue = new Integer[1024][512];
    BufferedReader r;

    public CoordinatesHandler(BufferedReader reader){
        r = reader;

    }

    public void update(){
        String line;
        int y=0;

        while(true){

            try {
                line = r.readLine();

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

    public Integer getProbability(Location l){

        return 1;
    }
}
