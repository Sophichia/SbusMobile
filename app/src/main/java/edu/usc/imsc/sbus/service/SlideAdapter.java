package edu.usc.imsc.sbus.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.WatchedItem;

/**
 * Created by Mengjia on 16/1/2.
 */
public class SlideAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<WatchedItem> mList = null;
    private OnDeleteListener mOnDeleteListener;
    private boolean delete = false;

    public SlideAdapter(Context mContext, ArrayList<WatchedItem> mList){
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setDelete(boolean delete){
        this.delete = delete;
    }

    public boolean isDelete(){
        return delete;
    }

    public void setOnDeleteListener(OnDeleteListener mOnDeleteListener){
        this.mOnDeleteListener = mOnDeleteListener;
    }

    public int getCount(){
        return mList.size();
    }

    public Object getItem(int pos) {
        return mList.get(pos);
    }

    public long getItemId(int pos){
        return pos;
    }

    public View getView(final int pos, View convertView, ViewGroup p){
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.watched_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.stopName = (TextView)convertView.findViewById(R.id.stopName);
            viewHolder.route = (TextView)convertView.findViewById(R.id.route);
            viewHolder.time = (TextView)convertView.findViewById(R.id.time);
            viewHolder.delete_action = (TextView)convertView.findViewById(R.id.delete_action);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnDeleteListener != null)
                    mOnDeleteListener.onDelete(pos);
            }
        };

        viewHolder.delete_action.setOnClickListener(mOnClickListener);
        viewHolder.stopName.setText(mList.get(pos).getStopName());
        viewHolder.route.setText(mList.get(pos).getRoute());
        viewHolder.time.setText(mList.get(pos).getTime());
        return convertView;

    }

    public static class ViewHolder{
        public TextView stopName;
        public TextView route;
        public TextView time;
        public TextView delete_action;
    }
}
