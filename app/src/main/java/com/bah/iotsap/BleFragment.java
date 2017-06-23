package com.bah.iotsap;

import android.app.Fragment;
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
    private ArrayAdapter<String> adapter;
    private ListView deviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        //Get the View
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ble_fragment, container, false);

        String[] deviceArray = {"test 1", "test2"};

        //initialize device list
        deviceList = (ListView) view.findViewById(R.id.ble_device_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
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


