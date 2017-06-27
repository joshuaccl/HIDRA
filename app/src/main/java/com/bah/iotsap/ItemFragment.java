package com.bah.iotsap;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends ListFragment {

    private static final String TAG = "ListFragment";

    private String               action;
    private ListView             listView;
    private ArrayAdapter<String> adapter;
    private List<String>         list;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive()");
            if(intent.getAction().equals(action)) {
                Log.i(TAG, "onReceive(): received action " + action);
                list.add(intent.getAction());
                adapter.notifyDataSetChanged();
            }
        }
    };

    public static ItemFragment newInstance() {
        return new ItemFragment();
    }

    /**
     * Use this method to instantiate new ItemFragments. It is very useful for
     * passing in any String to control what the fragment will listen to in
     * its receiver.
     *
     * @param action a String that the fragment will listen to in a receiver
     * @return new ItemFragment object with passed arguments available
     */
    public static ItemFragment newInstance(String action) {

        Log.i(TAG, "newInstance(): action: " + action);
        Bundle args = new Bundle();
        args.putString("action", action);
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.get("action") != null) {
            action = (String) savedInstanceState.get("action");
            Log.i(TAG, "onCreate(): received: " + action);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        // Set up adapter with the ListView
        if(list == null) list = new ArrayList<>();
        adapter = new ArrayAdapter<>(view.getContext(), R.layout.list_item, list);
        listView = getListView();
        setListAdapter(adapter);

        // Check the bundle for saved information or Strings
        Bundle bundle = getArguments();
        if(bundle != null && bundle.get("action") != null) {
            action = (String) bundle.get("action");
            Log.i(TAG, "onViewCreated(): action = " + action);
        }
    }
}
