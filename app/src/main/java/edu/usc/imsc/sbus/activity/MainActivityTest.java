package edu.usc.imsc.sbus.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import edu.usc.imsc.sbus.R;

/**
 * Created by Mengjia on 16/1/6.
 */
public class MainActivityTest extends ActionBarActivity {
    private MapView mMap;
    private ListView mListView;
    private IMapController mMapController;
    private LinearLayout layout_lv;
    private LinearLayout layout_map;
    private boolean tag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        mMap = (MapView)this.findViewById(R.id.map_test);
        mMapController = mMap.getController();
        mMap.setMultiTouchControls(true);
        mMap.setMinZoomLevel(14);
        mMap.setMaxZoomLevel(17);
        mMapController.setZoom(16);
        mMapController.setCenter(new GeoPoint(34.0205, -118.2856));

        mListView = (ListView)this.findViewById(R.id.lv_test);
        String[] strings = new String[]{"aaaaaaa","bbbbbb","CCCC","DDDDDDD"};
        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,strings));

        layout_lv = (LinearLayout)this.findViewById(R.id.layout_lv);
        layout_map = (LinearLayout)this.findViewById(R.id.layout_map);
        layout_lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layout_map.getLayoutParams();
                if(tag==false)
                    params.height=1200;
                else
                    params.height=1000;
                layout_lv.setLayoutParams(params);
                tag = !tag;
            }
        });


    }
}
