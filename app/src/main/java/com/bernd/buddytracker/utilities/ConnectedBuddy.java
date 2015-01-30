package com.bernd.buddytracker.utilities;

/**
 * Created by Bereza on 30.01.2015.
 */

import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pDevice;


public class ConnectedBuddy {
    private WifiP2pDevice dev;
    private Drawable profilePic;
    private String nickName;

    public ConnectedBuddy(WifiP2pDevice dev){
        this.dev=dev;
    }

    public WifiP2pDevice getDev() {
        return dev;
    }

    public Drawable getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Drawable profilePic) {
        this.profilePic = profilePic;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(ConnectedBuddy.class)) {
            ConnectedBuddy buddy = (ConnectedBuddy) obj;
            return buddy.getDev().deviceAddress.equals(dev.deviceAddress);
        }else{
            return false;
        }
    }

}
