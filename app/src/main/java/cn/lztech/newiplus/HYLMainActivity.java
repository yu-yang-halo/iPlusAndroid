package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import cn.lztech.jscontext.HYLJSContext;



public class HYLMainActivity extends Activity  implements LoginFragment.OnLoginHandler{
    private  static String ACTIVITY_LOG="HYLMainActivity";
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
    public void canLogin(boolean iscanlogin) {
        if (iscanlogin) {
            DeviceDetailFragment detailFragment=new DeviceDetailFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}
