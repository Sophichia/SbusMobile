package edu.usc.imsc.sbus.service;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Stop;

/**
 * Created by Mengjia on 16/1/7.
 */
public class MainActivityStopAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Stop> stopList = null;

    public MainActivityStopAdapter(Context mContext, ArrayList<Stop> mList) {
        this.mContext = mContext;
        this.stopList = mList;
    }

    public int getCount() {
        return stopList.size();
    }

    public Object getItem(int pos){
        return stopList.get(pos);
    }

    public long getItemId(int pos){
        return pos;
    }

    public View getView(final int pos, View convertView, ViewGroup p){
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.stop_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.stopImage = (ImageView)convertView.findViewById(R.id.stopImage);
            viewHolder.stopName = (TextView)convertView.findViewById(R.id.stop_name_item);
            viewHolder.stopInfo = (TextView)convertView.findViewById(R.id.stop_info_item);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ComparatorData comparatorData = new ComparatorData();
        Collections.sort(stopList,comparatorData);
        viewHolder.stopImage.setImageResource(R.drawable.ic_stop1);
        viewHolder.stopName.setText("Stop Name: " + stopList.get(pos).getName());
        viewHolder.stopInfo.setText("ID: "+stopList.get(pos).getId()+" · Including Route: 425 · Walk Time: 4min");
        viewHolder.stopName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
        viewHolder.stopInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        return convertView;
    }

    public static class ViewHolder{
        public ImageView stopImage;
        public TextView stopName;
        public TextView stopInfo;
    }

    public class ComparatorData implements Comparator{
        public int compare(Object o1, Object o2){
            Stop stop1 = (Stop) o1;
            Stop stop2 = (Stop) o2;
            if(stop1.getName().compareTo(stop2.getName())==0){
                return 0;
            }
            else if(stop1.getName().compareTo(stop2.getName())>0){
                return 1;
            }
            else
                return -1;
        }
    }
}
