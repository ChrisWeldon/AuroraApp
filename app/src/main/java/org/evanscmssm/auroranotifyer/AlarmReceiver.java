package org.evanscmssm.auroranotifyer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by chrisevans on 10/12/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Yay", "Alarm Received!!");
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);


        Intent startMain = new Intent(context, AuroraService.class);
        startMain.putExtra("URL", "https://mssm-cs-c9121.firebaseapp.com/aurora-nowcast-fake.txt");
        //startMain.putExtra("LAT", MainActivity.getDouble(settings, "LAT", 1 ));
        //startMain.putExtra("LON", MainActivity.getDouble(settings, "LON", 1 ));

        context.startService(startMain);

    }

}
