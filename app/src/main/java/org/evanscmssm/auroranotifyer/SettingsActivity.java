package org.evanscmssm.auroranotifyer;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.reflect.Array;



public class SettingsActivity extends AppCompatActivity {

    SharedPreferences settings;
    Spinner alarm_choice;
    EditText latEdit;
    EditText lonEdit;
    double tempLat, tempLon;
    SharedPreferences.Editor editor;
    boolean autoLoc;
    Switch autoLocSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        alarm_choice = (Spinner) findViewById(R.id.alarmChoice);
        ArrayAdapter<CharSequence> alarmAdapter = ArrayAdapter.createFromResource(this, R.array.notifysettings_array, android.R.layout.simple_spinner_item);
        alarmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarm_choice.setAdapter(alarmAdapter);
        latEdit = (EditText) findViewById(R.id.editLat);
        lonEdit = (EditText) findViewById(R.id.editLon);
        autoLocSwitch = (Switch)findViewById(R.id.switch1);

        latEdit.setSingleLine();
        lonEdit.setSingleLine();

        latEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        lonEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);


        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

        if(settings.contains("LAT")){
            double lat = MainActivity.getDouble(settings, "LAT", 10);
            double lon = MainActivity.getDouble(settings, "LON", 10);
            latEdit.setHint(Double.toString(lat));
            lonEdit.setHint(Double.toString(lon));
            tempLat = lat;
            tempLat = lon;
        }

        if(!settings.contains("ALOC")){
            editor.putBoolean("ALOC", false);
            autoLoc = false;
            autoLocSwitch.setChecked(false);
        }else{
            autoLoc = settings.getBoolean("ALOC", false);
            if(autoLoc){
                autoLocSwitch.setChecked(true);
            }
        }

        autoLocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    autoLoc = true;
                }else{
                    autoLoc = false;
                }
            }
        });

        latEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE && textView.getText().toString().length() > 0){
                    tempLat = Double.parseDouble(textView.getText().toString());
                    Log.d("Settings", textView.getText().toString());
                }
                return handled;
            }
        });

        lonEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE && textView.getText().toString().length() > 0){
                        tempLon = Double.parseDouble(textView.getText().toString());
                        Log.d("Settings", textView.getText().toString());

                }
                return handled;
            }
        });

        // TODO work on settings
        // TODO Make locator class
    }





    public void apply(View v){
        editor.putBoolean("ALOC", autoLoc);
        MainActivity.putDouble(editor,"LAT", tempLat);
        MainActivity.putDouble(editor,"LON", tempLon);

        //TODO fix Location being replaced with EnterLocation
        editor.commit();
        this.finish();
    }

    public void close(View v){
        this.finish();
    }

}
