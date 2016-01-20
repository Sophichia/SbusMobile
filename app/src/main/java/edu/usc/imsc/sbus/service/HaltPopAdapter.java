package edu.usc.imsc.sbus.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.TextView;

import java.util.List;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Halt;

/**
 * Created by Mengjia on 15/12/23.
 */
public class HaltPopAdapter extends BaseAdapter {
    private Context context;
    private List<Halt> list;
    public HaltPopAdapter(Context context, List<Halt> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    public Object getItem(int position){
        return list.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup){
        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.halt_pop_list_item,null);
            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.routeItem = (TextView) convertView.findViewById(R.id.routeItem);
            holder.timeItem = (TextView) convertView.findViewById(R.id.timeItem);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.routeItem.setText(list.get(position).getRoute());
        holder.timeItem.setText(list.get(position).getTime());

        return convertView;
    }

    static class ViewHolder{
        TextView routeItem;
        TextView timeItem;
    }

}
