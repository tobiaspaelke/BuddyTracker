package com.bernd.buddytracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Alex on 27.01.2015.
 */
public class BuddyManager {
    private static final String TAG = BuddyManager.class.getSimpleName();
    private static int PORT = 8888;

    //TODO persistent machen
    private ArrayList<ConnectedBuddy> buddyList = new ArrayList<>();

    private static BuddyManager instance;

    private BuddyManager() {
        instance = new BuddyManager();
    }

    public static BuddyManager getInstance() {
        return instance;
    }

    public void addConnectedBuddy(WifiP2pDevice dev) {
        //Drawable d = con.getResources().getDrawable(R.drawable.example);
    }

    public ConnectedBuddy getConnectedBuddy(WifiP2pDevice dev){
        int index = buddyList.indexOf(dev);
        if (index > -1){
            return buddyList.get(index);
        }
        return null;
    }

    public static class ConnectedBuddy {
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
        public boolean equals(Object o){
            if (o.getClass().equals(WifiP2pDevice.class)){
                WifiP2pDevice dev = (WifiP2pDevice) o;
                return this.getDev().deviceAddress.equals(dev.deviceAddress);
            }
            return false;
        }

    }

    public static class ProfilePicServerAsyncTask extends AsyncTask<Void, Integer, Drawable> {
        private Context context;
        private WifiP2pDevice dev;

        public ProfilePicServerAsyncTask(Context context, WifiP2pDevice dev) {
            this.context = context;
            this.dev = dev;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PORT);
                Socket client = serverSocket.accept();
                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */

                ObjectInputStream inputstream = new ObjectInputStream(client.getInputStream());
                Object receivedObject = inputstream.readObject();
                if (receivedObject.getClass().equals(Drawable.class))
                    return (Drawable) receivedObject;

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (serverSocket!=null)
                        serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable result){
            ConnectedBuddy buddy = BuddyManager.getInstance().getConnectedBuddy(dev);
            if (buddy!=null){
                buddy.setProfilePic(result);
            }
        }
    }
}
