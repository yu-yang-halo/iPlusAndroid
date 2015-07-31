package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.cache.HYLUserResourceConfig;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/17.
 */
public class LoginFragment extends Fragment {
    private  OnHYLWebHandler hylhandler;
    private  WebView webView;
    private  SwipeRefreshLayout  mSwipeLayout;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hylactivity_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.app_settings:
                hylhandler.toAppSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login, container, false);



        HYLUserResourceConfig.UserConfig userConfig=HYLUserResourceConfig.loadUserConfig(this.getActivity());

        if(userConfig==null){
            this.getActivity().getActionBar().setTitle(this.getActivity().getString(R.string.app_title));
        }else{
            this.getActivity().getActionBar().setTitle(userConfig.getTitle().getLogin());

            this.getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(userConfig.getBarColor())));

            int titleId = Resources.getSystem().getIdentifier(
                    "action_bar_title", "id", "android");
            TextView titleView = (TextView)this.getActivity().findViewById(titleId);
            titleView.setTextSize(userConfig.getFontSize());
            titleView.setTextColor(Color.parseColor(userConfig.getFontColor()));
        }



        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        webView=(WebView) view.findViewById(R.id.webview);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);


        String indexPath=HYLResourceUtils.rootPath(this.getActivity())+"ui/index.html";

        System.out.println("indexPath "+indexPath);

        webView.loadUrl(indexPath);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDefaultTextEncodingName("GBK");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                if (progress == 100) {
                    mSwipeLayout.setRefreshing(false);
                    Log.i("progress ",""+progress);
                }else{
                    if(!mSwipeLayout.isRefreshing()){
                        mSwipeLayout.setRefreshing(true);
                    }
                }
            }

        });

        HYLJSContext JSContext=new HYLJSContext(this.getActivity(),webView);
        JSContext.setCurrentHandler(new HYLJSContext.HYLJNAHandler() {
            @Override
            public void onSimpleCallback(HYLJSContext.JNAResult result) {
                if(!result.isSuc){
                    if(result.jsonString!=null&&!result.jsonString.equals("")){
                        Toast.makeText(getActivity(),result.jsonString,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(hylhandler!=null){
                        hylhandler.toDeviceList(result.isSuc);
                    }
                }


            }

            @Override
            public void onSaveBundle(Bundle bundle) {

            }

            @Override
            public void onRefreshDevice() {

            }
        });
        webView.addJavascriptInterface(JSContext, "jna");

        return view;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hylhandler=(OnHYLWebHandler)activity;
    }
}
