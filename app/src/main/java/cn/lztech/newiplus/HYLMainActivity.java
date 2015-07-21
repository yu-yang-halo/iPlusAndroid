package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.jscontext.HYLJSContext;



public class HYLMainActivity extends Activity  implements OnHYLWebHandler{
    private  static String ACTIVITY_LOG="HYLMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hylactivity_login);
        HYLSharePreferences.cacheDownloadDirName(this, null);


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            LoginFragment firstFragment = new LoginFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.fragment_container,firstFragment).commit();

        }


        //HYLResourceUtils.startDownloadUI(this,"zjdev1");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void canLogin(boolean iscanlogin) {
        if (iscanlogin) {
            DeviceDetailFragment detailFragment=new DeviceDetailFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    public void toDeviceInfo(Bundle bundle){
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

}
