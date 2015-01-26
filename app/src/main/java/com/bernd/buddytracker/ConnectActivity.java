package com.bernd.buddytracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ConnectActivity extends ActionBarActivity {
    private static final String TAG = ConnectActivity.class.getSimpleName();

    //wifiEnablingProgressDialog hat nur den Zweck ein Feedback zu geben, dass WIFI Status=Enabling, da ansonsten noch immer angezeigt wird, dass WLAN aus ist
    private ProgressDialog wifiEnablingProgressDialog;
    private ProgressDialog scanProgressDialog;
    private AlertDialog wifiAlertDialog;

    private ListView peerListView;
    private CountDownTimer countDownTimer;

    //WIFI Direct
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WifiDirectBroadcastReceiver wifiDirectReceiver;

    IntentFilter wifiDirectFilter;

    //Services setupt (DNS-Service Discovery)
    public static final String SERVICE_INSTANCE = "_buddytracker";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        //Dialoge bauen
        wifiAlertDialog = createWifiAlertDialog();
        wifiEnablingProgressDialog = createProgressDialog(getString(R.string.loading),getString(R.string.wdirect_activation));
        scanProgressDialog = createProgressDialog(getString(R.string.searchPeers),"");

        wifiDirectFilter = new IntentFilter();
        wifiDirectFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiDirectFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //WIFI Direct setup
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        wifiDirectReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        final Button btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan(btn_scan);
            }
        });

        //ListView und Adapter verbinden
        peerListView = (ListView) findViewById(R.id.listView_peerlist);
        peerListView.setAdapter(new WifiP2pDeviceAdapter(this));
        peerListView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                scanProgressDialog.dismiss();
                countDownTimer.cancel();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if (peerListView.getAdapter().getCount()==0)
                    btn_scan.setVisibility(View.VISIBLE);
            }
        });
        peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                final WifiP2pDevice dev = (WifiP2pDevice) peerListView.getItemAtPosition(position);

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = dev.deviceAddress;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ConnectActivity.this,"Verbindung mit " + dev.deviceName + " initialisiert",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(ConnectActivity.this,"Verbindung mit " + dev.deviceName + " konnte nicht intialisiert werden",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        startScan(btn_scan);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkWifiStatus();
        //WifiDirectReceiver registrieren
        registerReceiver(wifiDirectReceiver, wifiDirectFilter);
        //Lokalen Service starten
        startRegistration();
    }

    @Override
    public void onPause() {
        super.onPause();
        //WifiDirectReceiver entfernen
        unregisterReceiver(wifiDirectReceiver);
        wifiAlertDialog.dismiss();
        wifiEnablingProgressDialog.dismiss();
        scanProgressDialog.dismiss();
        countDownTimer.cancel();
        //Lokalen Service entfernen
        clearLocalServices();
        //Suche nach Services einstellen
        clearRequests();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onStop();
    }

    /**
     * Baut den Alertdialog, der einen zwingen soll WIFI einzuschalten
     *
     * @return liefert den fertigen Alertdialog zurück
     */
    private AlertDialog createWifiAlertDialog() {
        AlertDialog.Builder alertDiaBuilder = new AlertDialog.Builder(this);

        alertDiaBuilder.setTitle(getString(R.string.wlanOn));
        alertDiaBuilder.setMessage(getString(R.string.goToSettings));
        alertDiaBuilder.setCancelable(false);

        alertDiaBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // WLAN einstellungen öffnen
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });

        alertDiaBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        alertDiaBuilder.setOnKeyListener(new AlertDialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                //Wenn zurück Button gedrückt, beende Activity
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return true;
            }
        });
        return alertDiaBuilder.create();
    }

    /**
     * Baut den Progressdioalog, der angezeigt wird, solange WLAN P2P noch nicht verfügbar ist und WIFI sich in Modul ENABLING befindet
     *
     * @return liefert den fertigen Progressdialog zurück
     */
    private ProgressDialog createProgressDialog(String title, String message) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setTitle(title);
        progress.setMessage(message);
        progress.setOnKeyListener(new AlertDialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                //Wenn zurück Button gedrückt, beende Activity
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return true;
            }
        });
        return progress;
    }

    /**
     * Checkt nach onResume den WIFI status und öffnet entsprechende Dialoge
     *
     */
    public void checkWifiStatus() {
        WifiManager wifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        int state = wifiMgr.getWifiState();

        if ((state == WifiManager.WIFI_STATE_ENABLING)) {
            // WIFI wird gerade eingeschalten
            wifiEnablingProgressDialog.show();
        } else if (state != WifiManager.WIFI_STATE_ENABLED) {
            // WIFI ist aus
            wifiAlertDialog.show();
        }
    }

    /**
     * Startet den Scan
     *
     * @param btn_scan      Scan Button
     */
    public void startScan(final Button btn_scan){
        btn_scan.setVisibility(View.GONE);
        scanProgressDialog.show();

        countDownTimer = new CountDownTimer(30000,3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                startServiceDiscovery();
            }

            @Override
            public void onFinish() {
                scanProgressDialog.dismiss();
                btn_scan.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    /**
     *  Lokalen Service hinzufügen mit Nickname als Zusatzinfo
     */
    private void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(ProfileSettingsActivity.propNickname, ProfileSettingsActivity.exampleNickName1);

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        mManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Local Service hinzugefügt");
            }

            @Override
            public void onFailure(int error) {
                Log.e(TAG, "Local Service nicht hinzugefügt");
            }
        });
    }

    /**
     * Nach verfügbaren Services suchen
     */
    private void startServiceDiscovery() {
        //Listener registrieren, die aufgerufen werden, wenn ein Service gefunden wird
        mManager.setDnsSdResponseListeners(mChannel, null, new WifiP2pManager.DnsSdTxtRecordListener() {
                    //Wird aufgerufen, wenn ein Service mit Zusatzinfos gefunden wurde
                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
                        //entspricht der Service unserem BuddyTracker?
                        if (fullDomainName.contains(SERVICE_INSTANCE)){
                            Log.d(TAG, device.deviceName + " is " + record.get(ProfileSettingsActivity.propNickname));
                            String nick = record.get(ProfileSettingsActivity.propNickname);
                            WifiP2pDeviceAdapter adapter = ((WifiP2pDeviceAdapter) peerListView.getAdapter());
                            adapter.addAvailableBuddy(device, nick);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


        //Service Request erstellen und hinzufügen
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Added service discovery request");
                        //Request hinzugefügt --> Discovery starten
                        discoverServices();
                    }
                    @Override
                    public void onFailure(int arg0) {
                        Log.e(TAG,"Failed adding service discovery request");
                    }
                });

    }

    /**
     * Startet Suche nach Services
     */
    private void discoverServices(){
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                Log.e(TAG, "Service discovery failed: " + arg0);
            }
        });
    }

    /**
     * Entfernt lokale Services
     */
    private void clearLocalServices(){
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"Local Services cleared");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG,"Local Services clear failed");
            }
        });
    }

    /**
     * Entfernt alle Service requests.
     *
     * ACHTUNG!!!!!!!! Dieser Bug hat mich das ganze wochenende beschäftigt:
     * Wenn man vorher nicht stopPeerDiscovery() aufruft, welche man vorher ja nie gestartet hat, dann funktioniert
     * die Suche nur beim ersten mal und danach finden sich die Handys nicht mehr
     */
    private void clearRequests(){
        //Muss aus unerklärlichen Gründen gemacht werden
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Service requests entfernen
                mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Service requests cleared");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG,"Service requests clear failed");
                    }
                });
            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG, "FAILED to stop discovery");
            }
        });
    }



    //Getters
    public ProgressDialog getScanProgressDialog() {
        return scanProgressDialog;
    }

    public CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public AlertDialog getWifiAlertDialog() {
        return wifiAlertDialog;
    }

    public ProgressDialog getWifiEnablingProgressDialog() {
        return wifiEnablingProgressDialog;
    }

    public ListView getPeerListView() {
        return peerListView;
    }
}
