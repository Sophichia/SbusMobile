package edu.usc.imsc.sbus.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Stop;
import edu.usc.imsc.sbus.basicClass.Vehicle;

/**
 * Created by danielCantwell on 4/8/15.
 */
public class VehicleInfoDialog extends DialogFragment {

    private Vehicle mVehicle;

    public VehicleInfoDialog() {

    }

    public void setVehicle(Vehicle v) {
        mVehicle = v;
    }

    private TextView name;
    private TextView nextStop;
    private TextView arrivalTime;

    private ListView stopsList;
    private StopsAdapter mAdapter;
    private List<Stop> mUpcomingStops;

    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_vehicleinfo, null);

        name = (TextView) view.findViewById(R.id.name);
        nextStop = (TextView) view.findViewById(R.id.next_stop);
        arrivalTime = (TextView) view.findViewById(R.id.arrival_time);
        stopsList = (ListView) view.findViewById(android.R.id.list);

        mUpcomingStops = new ArrayList<>();
        for (Stop s : mVehicle.getStops()) {
            if (mVehicle.getNextStop()< s.getSequence() - 1) {
                mUpcomingStops.add(s);
            }
        }

        mAdapter = new StopsAdapter(getActivity(), android.R.id.list, mUpcomingStops);
        stopsList.setAdapter(mAdapter);

        name.setText(mVehicle.getStopHeadsign());
        nextStop.setText(mVehicle.getStops().get(mVehicle.getNextStop()).getName());
        arrivalTime.setText(mVehicle.getStops().get(mVehicle.getNextStop()).getArrivalTime());

        builder.setView(view);
        return builder.create();
    }
}