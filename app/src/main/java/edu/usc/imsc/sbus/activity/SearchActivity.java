package edu.usc.imsc.sbus.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.usc.imsc.sbus.R;

/**
 * Created by Mengjia on 16/1/13.
 */
public class SearchActivity extends ActionBarActivity{
    private TextView tvSearchLocation;
    private TextView tvSearchRoute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvSearchLocation = (TextView)findViewById(R.id.tv_searchLocation);
        tvSearchRoute = (TextView)findViewById(R.id.tv_searchRoute);
        selectLocationFragment();
        tvSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocationFragment();
            }
        });

        tvSearchRoute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectRouteFragment();
            }
        });


    }

    public void selectLocationFragment(){
        Fragment fragment = new LocationFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
    }

    public void selectRouteFragment(){
        Fragment fragment = new RouteFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
    }
}
