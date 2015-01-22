package com.bernd.buddytracker;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.test.RenamingDelegatingContext;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Alex on 22.01.2015.
 */
public class WifiP2pDeviceAdapter extends BaseAdapter {
    private ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();

    private final LayoutInflater inflator;

    public void updateDeviceList(Collection<WifiP2pDevice> collection){
        devices = new ArrayList<WifiP2pDevice>(collection);
    }

    public WifiP2pDeviceAdapter(Context context){
        // wird für das aufblasen der XML Datei benötigt
        inflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            //Layoutdatei entfalten
            convertView = inflator.inflate(R.layout.item_peer_text_only, parent, false);

            //Holder erzeugen
            holder = new ViewHolder();
            holder.text1 = (TextView) convertView.findViewById(R.id.item_peer_text1);

            convertView.setTag(holder);
        }else{
            // Holder bereits vorhanden
            holder = (ViewHolder) convertView.getTag();
        }

        WifiP2pDevice device = (WifiP2pDevice) getItem(position);
        holder.text1.setText(device.deviceName);

        return convertView;
    }

    static class ViewHolder{
        TextView text1;
    }
}
