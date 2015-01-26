package com.bernd.buddytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

/**
 * Created by Tobias on 22.01.2015.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = WifiDirectBroadcastReceiver.class.getSimpleName();

    //schaltet Debugausgaben ein und aus
    private boolean DEBUG = true;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ConnectActivity mActivity;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ConnectActivity activity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //----WIFI Statusänderungen----
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

            //if (mActivity.active) {
                if ((state == WifiManager.WIFI_STATE_ENABLING)) {
                    // WIFI wird gerade eingeschalten
                    mActivity.getWifiAlertDialog().dismiss();
                    mActivity.getWifiEnablingProgressDialog().show();
                } else if (state != WifiManager.WIFI_STATE_ENABLED) {
                    // WIFI ist aus
                    mActivity.getWifiAlertDialog().show();
                }
            //}
        } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //WIFI ist an
            int p2pState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if ((p2pState == WifiP2pManager.WIFI_P2P_STATE_ENABLED)/* && (mActivity.active)*/){
                mActivity.getWifiEnablingProgressDialog().dismiss();
            }
        }



        //-----WIFI Direct----
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            //updatePeerList();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            WifiP2pInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            if (info.groupFormed) {
                Toast.makeText(mActivity, "Verbindung hergestellt", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mActivity, "Verbindung getrennt", Toast.LENGTH_SHORT).show();
            }
            //updatePeerList();

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    /*private void updatePeerList(){
        if (mManager != null) {
            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    if (DEBUG)
                        Log.d(TAG, "peerlistener wurde aufgerufen");
                    Collection<WifiP2pDevice> devices = peers.getDeviceList();
                    if (DEBUG) {
                        for (WifiP2pDevice dev : devices) {
                            Log.d(TAG, dev.deviceName);
                        }
                    }

                    //aktualisierte Peers dem Adapter übergeben
                    WifiP2pDeviceAdapter adapter = (WifiP2pDeviceAdapter) mActivity.getPeerListView().getAdapter();
                    adapter.updateDeviceList(devices);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }*/
}