package edu.usc.imsc.sbus.service;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Vehicle;

/**
 * Created by danielCantwell on 4/27/15.
 */
public class VehiclesAdapter extends ArrayAdapter<Vehicle> {

    private class ViewHolder {
        TextView title;
        TextView description;
    }

    private Context mContext;
    private int mResourceId;
    private List<Vehicle> mVehicles;

    public VehiclesAdapter(Context context, int resource, List<Vehicle> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
        mVehicles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResourceId, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Vehicle v = mVehicles.get(position);
        holder.title.setText(v.getStopHeadsign());
        holder.description.setText(v.getStops().get(v.getNextStop()).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Vehicle)(resultValue)).getStopHeadsign();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<Vehicle> suggestions = new ArrayList<>();
                for (Vehicle customer : mVehicles) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (customer.getStopHeadsign().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Vehicle> filteredList = (ArrayList<Vehicle>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Vehicle v : filteredList) {
                    add(v);
                }
                notifyDataSetChanged();
            }
        }
    };
}
