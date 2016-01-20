package edu.usc.imsc.sbus.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.WatchedItem;
import edu.usc.imsc.sbus.dao.DatabaseContract;
import edu.usc.imsc.sbus.service.ActionSheet;
import edu.usc.imsc.sbus.service.DatabaseHelper;
import edu.usc.imsc.sbus.service.ListViewOnSingleTapUpListenner;
import edu.usc.imsc.sbus.service.OnDeleteListener;
import edu.usc.imsc.sbus.service.SlideAdapter;
import edu.usc.imsc.sbus.widget.DelSlideListView;

/**
 * Created by Mengjia on 15/12/31.
 */
public class WatchedItemActivity extends ActionBarActivity implements OnDeleteListener, ListViewOnSingleTapUpListenner, ActionSheet.OnActionSheetSelected, DialogInterface.OnCancelListener {
    private Toolbar toolbar;
    private ArrayList<WatchedItem> mlist = new ArrayList<>();
    private SlideAdapter mySlideAdapter;
    DelSlideListView mDelSlideListView;
    int delId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watched_item);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //load the watchedItem
        mlist = getAllWatchedItems();

        mDelSlideListView = (DelSlideListView)this.findViewById(R.id.itemList);
        mySlideAdapter = new SlideAdapter(this,mlist);
        mDelSlideListView.setAdapter(mySlideAdapter);
        mDelSlideListView.setOnDeleteListener(this);
        mDelSlideListView.setSingleTapUpListener(this);
        mySlideAdapter.setOnDeleteListener(this);


    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public void onSingleTapUp(){

    }

    @Override
    public boolean isCanDelete(int position){
        return true;
    }

    @Override
    public void onDelete(int ID){
        delId = ID;
        ActionSheet.showSheet(this,this,this);
    }

    @Override
    public void onBack(){

    }

    @Override
    public void onCancel(DialogInterface arg){

    }

    @Override
    public void onClick(int whichButton){
        switch (whichButton){
            case 0:
                //delete the item from the database
                DatabaseHelper dbh = new DatabaseHelper(WatchedItemActivity.this);
                dbh.deleteWatchedItem(mlist.get(delId).getStopId());
                dbh.close();
                //delete the item from the view
                mlist.remove(delId);
                mDelSlideListView.deleteItem();
                mySlideAdapter.notifyDataSetChanged();
                break;
            case 1:
                break;
            default:
                break;
        }
    }
    /**
     * MENU / APP BAR FUNCTIONS
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_watched_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.back:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<WatchedItem> getAllWatchedItems(){
        DatabaseHelper dbh = new DatabaseHelper(WatchedItemActivity.this);
        Cursor cursor = dbh.retrieveAllHalt();
        ArrayList<WatchedItem> watchedItemList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String stopId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataHalt.COLUMN_NAME_STOP_ID));
                String stopName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataHalt.COLUMN_NAME_STOP_NAME));
                String route = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataHalt.COLUMN_NAME_ROUTE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataHalt.COLUMN_NAME__TIME));
                watchedItemList.add(new WatchedItem(stopId,stopName,route,time));
                Log.d("Pop test", "stopName is " + stopName);
                Log.d("Pop test","route is "+route);
                Log.d("Pop test","time is "+time);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return watchedItemList;
    }
}
