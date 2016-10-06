package org.evanscmssm.auroranotifyer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;


public class MainActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{


    Downloader downloader;
    GoogleApiClient mGoogleApiClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //downloader = new Downloader();

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},1
                    /*LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION*/);
        }
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

    //TextView mLatitudeText = (TextView)findViewById(R.id.latText);
    //TextView mLongitudeText =(TextView)findViewById(R.id.lonText);;
    Location mLastLocation = null;

    String lat, lon;

    public void updateLoc(View w){

    }

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
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
                updateUI();
            }
        }catch(SecurityException e){
            Log.d("Log", "Security Exception");
        }


    }



    public void onLocationChanged(Location l){
        updateUI();
    }

    void updateUI() {
        Log.d("Log", lat);
        Log.d("Log", lon);
        //mLatitudeText.setText(lat);
        //mLongitudeText.setText(lon);
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

        downloader.downloadURL(this, "http://services.swpc.noaa.gov/text/aurora-nowcast-map.txt");

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
