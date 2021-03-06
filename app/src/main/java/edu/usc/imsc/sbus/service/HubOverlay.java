package edu.usc.imsc.sbus.service;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.activity.MainActivity;
import edu.usc.imsc.sbus.basicClass.Hub;

/**
 * Created by Mengjia on 16/1/19.
 */
public class HubOverlay extends DataRequestListener.MapOverlay {

    public enum Type{
        Normal, Active
    }

    private Type mType;

    public HubOverlay(MainActivity context, DataRequestListener.MapClickListener listener, Type type){
        super();
        mContext = context;
        mListener = listener;
        mHidden = false;
        mType = type;
        mItems = new ArrayList<>();
        ArrayList<OverlayItem> items = new ArrayList<>();
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(mContext);

        mIcon = mContext.getResources().getDrawable(mType == Type.Normal ? HubOverlayItem.iconId : R.drawable.stop_large);
        mActiveIcon = mContext.getResources().getDrawable(mType == Type.Normal ? HubOverlayItem.focusedIconId : R.drawable.stop_active_large );

        mOverlay = new ItemizedIconOverlay<>(
                items, mIcon,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
                    @Override
                    public boolean onItemSingleTapUp(int i, OverlayItem myOverlayItem){
                        return handleHubClick((HubOverlayItem) myOverlayItem);
                    }

                    @Override
                    public  boolean onItemLongPress(int i, OverlayItem myOverlayItem){
                        return true;
                    }
                },resourceProxy);
    }

    private boolean handleHubClick(HubOverlayItem item){
        Hub h = item.hub;

        if(mActiveItem != null){

            mActiveItem.setMarker(mIcon);

        }


        mActiveItem = item;
        mActiveItem.setMarker(mActiveIcon);

        mListener.onHubClick(h);
        return true;
    }
}
