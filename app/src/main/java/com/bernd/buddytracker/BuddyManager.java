package com.bernd.buddytracker;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;

/**
 * Created by Bereza on 27.01.2015.
 */
public class BuddyManager {
    //TODO persistent machen
    ArrayList<ConnectedBuddy> buddyList = new ArrayList<>();

    private static BuddyManager instance;

    private BuddyManager(){
        instance = new BuddyManager();
    }

    public static BuddyManager getInstance(){
        return instance;
    }

    public void addConnectedBuddy(WifiP2pDevice dev){

    }

    public static class ConnectedBuddy{

    }
}
