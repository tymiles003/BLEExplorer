package com.devork.ble.explorer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.devork.ble.beacon.IBeacon;
import com.devork.ble.beacon.IBeaconListener;
import com.devork.ble.beacon.IBeaconProtocol;

import java.util.concurrent.atomic.AtomicBoolean;


public class BLEExplorerActivity extends Activity implements IBeaconListener {


    MenuItem bleScan;
    ListView beacons;

    AtomicBoolean scanning = new AtomicBoolean(false);

    MySimpleArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleexplorer);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        IBeaconProtocol.getInstance(this).setListener(this);

        adapter = new MySimpleArrayAdapter(this);

        beacons = (ListView)findViewById(R.id.lv_beacons);
        beacons.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bleexplorer, menu);
        bleScan = menu.findItem(R.id.action_scan);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_scan) {
            if (!scanning.get()) {
                adapter.clear();
                bleScan.setIcon(R.drawable.ic_action_bluetooth_searching);
                scanning.set(true);
                IBeaconProtocol.getInstance(this).scanIBeacons(true);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void enterRegion(IBeacon ibeacon) {

    }

    @Override
    public void exitRegion(IBeacon ibeacon) {

    }

    @Override
    public void beaconFound(final IBeacon ibeacon) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(ibeacon);
            }
        });

    }

    @Override
    public void searchState(int state) {
        if (state != IBeaconProtocol.SEARCH_STARTED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bleScan.setIcon(R.drawable.ic_action_bluetooth);
                    adapter.notifyDataSetChanged();
                }
            });

            scanning.set(false);
        }
    }

    @Override
    public void operationError(int status) {

    }

    public class MySimpleArrayAdapter extends ArrayAdapter<IBeacon> {
        private final Context context;

        public MySimpleArrayAdapter(Context context) {
            super(context, R.layout.beacon_list_item);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;

            IBeacon beacon = getItem(position);


            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.beacon_list_item, parent, false);
            }

            TextView uuid = (TextView) rowView.findViewById(R.id.tv_uuid);
            TextView mac = (TextView) rowView.findViewById(R.id.tv_mac);
            TextView major = (TextView) rowView.findViewById(R.id.tv_major);
            TextView minor = (TextView) rowView.findViewById(R.id.tv_minor);
            TextView strength = (TextView) rowView.findViewById(R.id.tv_strength);
            TextView distance = (TextView) rowView.findViewById(R.id.tv_distance);

            uuid.setText(beacon.getUuidHexStringDashed());
            mac.setText(beacon.getMacAddress());
            major.setText("Major: " + beacon.getMajor());
            minor.setText("Minor: " + beacon.getMinor());
            strength.setText("Strength: " + beacon.getPowerValue());
            distance.setText("Proximity: " + beacon.getProximity());

            return rowView;
        }
    }


}
