package com.bernd.buddytracker;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListView;


public class CheckStatusActivity extends ListActivity {
    private static final String TAG = CheckStatusActivity.class.getSimpleName();

    private IntentFilter intentFilter;
    private BuddyListReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_check_status);

        getListView().setAdapter(new ConnectedBuddyAdapter(this));

        intentFilter = new IntentFilter(BuddyManager.CONNECTED_BUDDIES_CHANGED_ACTION);


        //TODO broadcastreceiver für buddy list changed
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver = new BuddyListReceiver(this),intentFilter);
    }

    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_status, menu);
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

    private static class BuddyListReceiver extends BroadcastReceiver{
        ListActivity mActivity;

        public BuddyListReceiver(ListActivity ac){
            this.mActivity=ac;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Bild wurde empfangen");
            ConnectedBuddyAdapter adapter = (ConnectedBuddyAdapter) mActivity.getListAdapter();
            adapter.notifyDataSetChanged();
        }
    }
}
