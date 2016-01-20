package edu.usc.imsc.sbus.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Stop;

/**
 * Created by danielCantwell on 4/23/15.
 */
public class StopsAdapter extends ArrayAdapter<Stop> {

    private class ViewHolder {
        TextView stopName;
        TextView stopTime;
    }

    private Context context;

    public StopsAdapter(Context context, int resource, List<Stop> stops) {
        super(context, resource, stops);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Stop stop = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.stop_info, null);
            holder = new ViewHolder();
            holder.stopName = (TextView) convertView.findViewById(R.id.selected_stop_name);
            holder.stopTime = (TextView) convertView.findViewById(R.id.selected_stop_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.stopName.setText(stop.getName());
        holder.stopTime.setText(stop.getArrivalTime());

        return convertView;
    }
}
