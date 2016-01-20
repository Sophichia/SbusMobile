package edu.usc.imsc.sbus.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.usc.imsc.sbus.R;

/**
 * Created by Mengjia on 16/1/13.
 */
public class LocationFragment extends Fragment {


    public LocationFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_search_location,container,false);

        return rootView;
    }
}
