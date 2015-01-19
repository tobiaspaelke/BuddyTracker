package com.bernd.buddytracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ConnectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
    }

    //Sichert ab, dass WLAN an ist
    @Override
    protected void onResume(){
        super.onResume();
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //Wenn WLAN aus ist, soll es eingeschalten werden, oder man landet im hauptmenü
        if (!mWifi.isConnected()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            //Dialog Titel
            alertDialog.setTitle("Wifi muss eingeschaltet sein");

            //Dialog Message
            alertDialog.setMessage("Zu Einstellungen wechseln?");

            alertDialog.setPositiveButton("ja",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // WLAN einstellungen öffnen
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            alertDialog.setNegativeButton("nein",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
            alertDialog.show();
        }
    }
}
