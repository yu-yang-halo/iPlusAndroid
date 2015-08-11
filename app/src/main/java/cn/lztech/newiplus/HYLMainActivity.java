package cn.lztech.newiplus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

import cn.lztech.WifiAdmin;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.cache.HYLUserResourceConfig;
import cn.lztech.jscontext.HYLJSContext;



public class HYLMainActivity extends Activity  implements OnHYLWebHandler{
    private  static String ACTIVITY_LOG="HYLMainActivity";
    HYLPage currentPage;
    Bundle deviceBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hylactivity_login);



        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            LoginFragment firstFragment = new LoginFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.fragment_container,firstFragment).commit();

        }

    }





    public void toDeviceList(boolean iscanlogin) {
        if (iscanlogin) {
            //缓存网络ssid
            WifiAdmin wifiAdmin=new  WifiAdmin(this);
            HYLSharePreferences.setWIFISSID(this,wifiAdmin.getSSID());

            DeviceListFragment detailFragment=new DeviceListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    public void toDeviceInfo(Bundle bundle){
        deviceBundle=bundle;
        int objectId= bundle.getInt(HYLJSContext.key_objectId);
        if(objectId>0){
            DeviceInfoFragment deviceInfoFragment=new DeviceInfoFragment();
            deviceInfoFragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, deviceInfoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    @Override
    public void toDeviceConfig(Bundle bundle) {
        int objectId= bundle.getInt(HYLJSContext.key_objectId);
        if(objectId>0){
            DeviceConfigFragment deviceConfigFragment=new DeviceConfigFragment();
            deviceConfigFragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, deviceConfigFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    @Override
    public void toWifiConfig() {
        HYLWifiConfigFragment hylWifiConfigFragment=new HYLWifiConfigFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, hylWifiConfigFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void toAppSettings() {
        HYLSettingFragment hylSettingFragment=new HYLSettingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, hylSettingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
