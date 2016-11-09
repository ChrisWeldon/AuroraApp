package org.evanscmssm.auroranotifyer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{


    AlarmService As;
    GoogleApiClient mGoogleApiClient = null;
    TextView textProb;
    TextView textLat;
    TextView textLon;
    BroadcastReceiver probabilityReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textProb = (TextView) findViewById(R.id.mProb);
        textLat = (TextView) findViewById(R.id.mLat);
        textLon = (TextView) findViewById(R.id.mLon);

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

        probabilityReceiver = new BroadcastReceiver(){

            public void onReceive(Context context, Intent intent){
                Log.d("Main Activity", "Got Broadcast to get probability");
                updateSettings();
            }

        };
    }

    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();

    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    LocationRequest mLocationRequest;


    Location mLastLocation = null;

    String mlat, mlon;
    double lat;
    double lon;
    SharedPreferences settings;



    @Override
    public void onConnected(Bundle connectionHint){

        try {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000); // Update location every second

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d("Log", "Setting Text");
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
                mlat = String.valueOf(lat);
                mlon = String.valueOf(lon);
                settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                putDouble(editor, "LAT", lat );
                putDouble(editor, "LON", lon );
                editor.commit();
                updateSettings();
            }
        }catch(SecurityException e){
            Log.d("Log", "Security Exception");
        }


    }

    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        if ( !prefs.contains(key))
            return defaultValue;

        return Double.longBitsToDouble(prefs.getLong(key, 5));
    }




    public void onLocationChanged(Location l){
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        putDouble(editor, "LAT", lat );
        putDouble(editor, "LON", lon );
        editor.commit();
        updateSettings();

    }

    void updateSettings() {
        Log.d("Log", mlat);
        Log.d("Log", mlon);
        textLat.setText(mlat);
        textLon.setText(mlon);
        textProb.setText(Integer.toString(settings.getInt("PROB", 50)));

    }




    @Override
    public void onConnectionSuspended(int i){
        Log.d("Log", "ConnectionSuspended");

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.d("Log", "Connection Failed");
    }


    public void myHandler(View w){
        As = new AlarmService(this);
        As.startAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
