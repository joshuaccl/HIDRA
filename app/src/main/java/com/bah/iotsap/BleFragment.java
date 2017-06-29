package com.bah.iotsap;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by 591263 on 6/22/2017.
 */

public class BleFragment extends Fragment {

    //Declare variables
    private static final String TAG = "BleFragment";
    private ArrayAdapter<BluetoothDevice> adapter;
    private ArrayList<BluetoothDevice> deviceArray;
    private ListView deviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        //Get the View
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ble_fragment, container, false);

        //initialize device list
        deviceList = (ListView) view.findViewById(R.id.ble_device_list);

        ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<BluetoothDevice>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                deviceArray
        );

        //Set the adapter
        deviceList.setAdapter(adapter);

        //return the view
        return view;

    }
}


