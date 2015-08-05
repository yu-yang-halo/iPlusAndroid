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
    RelativeLayout  navigationBar;
    Button rightBtn;
    public Button leftBtn;
    TextView titleText;
    HYLPage currentPage;
    Bundle deviceBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hylactivity_login);
        navigationBar= (RelativeLayout) findViewById(R.id.navigationBar);
        rightBtn= (Button) findViewById(R.id.rightBtn);
        leftBtn= (Button) findViewById(R.id.leftBtn);
        titleText= (TextView) findViewById(R.id.titleText);


        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage==HYLPage.HYL_PAGE_LOGIN){
                    toAppSettings();
                }else if(currentPage==HYLPage.HYL_PAGE_DEVICE_LIST){
                    toWifiConfig();
                }else if(currentPage==HYLPage.HYL_PAGE_DEVICE_INFO){
                    if(deviceBundle!=null) {
                        toDeviceConfig(deviceBundle);
                    }
                }
            }
        });


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
            transaction.replace(R.id.fragment_container,detailFragment);
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
            transaction.replace(R.id.fragment_container,deviceInfoFragment);
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
            transaction.replace(R.id.fragment_container,deviceConfigFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    @Override
    public void toWifiConfig() {

        HYLWifiConfigFragment hylWifiConfigFragment=new HYLWifiConfigFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,hylWifiConfigFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void toAppSettings() {
        HYLSettingFragment hylSettingFragment=new HYLSettingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,hylSettingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void doSomethingAtCuttentPage(HYLPage currentPage,Bundle deviceBundle) {
         this.currentPage=currentPage;
         if(this.currentPage==HYLPage.HYL_PAGE_LOGIN){
            this.leftBtn.setVisibility(View.GONE);
         }else{
            this.leftBtn.setVisibility(View.VISIBLE);
             if(this.currentPage==HYLPage.HYL_PAGE_DEVICE_CONFIG){
                 if(deviceBundle!=null){
                     leftBtn.setText(deviceBundle.getString(HYLJSContext.key_deviceName));
                 }
             }else{
                 leftBtn.setText("返回");
             }
         }
        if(this.currentPage==HYLPage.HYL_PAGE_APP_SYS_CONFIG||this.currentPage==HYLPage.HYL_PAGE_WIFI_DEVICE_CONFIG||this.currentPage==HYLPage.HYL_PAGE_DEVICE_CONFIG){
            this.rightBtn.setVisibility(View.GONE);
        }else{
            this.rightBtn.setVisibility(View.VISIBLE);
        }

        if(this.currentPage==HYLPage.HYL_PAGE_LOGIN){
            rightBtn.setBackgroundResource(R.drawable.bg_button_style1);
            rightBtn.setText("");
        }else{
            rightBtn.setBackgroundResource(R.color.transparent);
        }

        HYLUserResourceConfig.UserConfig userConfig=HYLUserResourceConfig.loadUserConfig(this);
        switch (currentPage){
            case HYL_PAGE_APP_SYS_CONFIG:
                titleText.setText(this.getString(R.string.app_settings));
                break;
            case HYL_PAGE_DEVICE_CONFIG:
                titleText.setText("设置");

                break;
            case  HYL_PAGE_DEVICE_INFO:
                if(deviceBundle!=null){
                    titleText.setText(deviceBundle.getString(HYLJSContext.key_deviceName));
                }
                rightBtn.setText("设置");
                break;
            case  HYL_PAGE_DEVICE_LIST:
                rightBtn.setText("配置");
                if(userConfig==null){
                    titleText.setText("设备列表");
                }else{
                    titleText.setText(userConfig.getTitle().getDevices());
                }
                break;
            case  HYL_PAGE_LOGIN:

                if(userConfig==null){
                    titleText.setText(getString(R.string.app_title));
                }else{
                    titleText.setText(userConfig.getTitle().getLogin());
                    navigationBar.setBackgroundColor(Color.parseColor(userConfig.getBarColor()));
                    titleText.setTextSize(userConfig.getFontSize());
                    titleText.setTextColor(Color.parseColor(userConfig.getFontColor()));
                }
                break;
            case HYL_PAGE_WIFI_DEVICE_CONFIG:
                titleText.setText(this.getString(R.string.wifi_settings));
                break;
        }

    }

}
