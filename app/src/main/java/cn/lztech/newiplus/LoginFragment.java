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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.cache.HYLUserResourceConfig;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/17.
 */
public class LoginFragment extends HeaderFragment {
    private  OnHYLWebHandler hylhandler;
    private  WebView webView;
    private  SwipeRefreshLayout  mSwipeLayout;

    @Override
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        rightBtn= (Button) view.findViewById(R.id.rightBtn);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        titleText= (TextView) view.findViewById(R.id.titleText);


        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity().onBackPressed();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hylhandler.toAppSettings();
            }
        });

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login, container, false);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        webView=(WebView) view.findViewById(R.id.webview);

        initHeaderView(view);

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
    public void onStart() {
        super.onStart();
        if(userConfig==null){
            titleText.setText(getString(R.string.app_title));
        }else{
            titleText.setText(userConfig.getTitle().getLogin());
            navigationBar.setBackgroundColor(Color.parseColor(userConfig.getBarColor()));
            titleText.setTextSize(userConfig.getFontSize());
            titleText.setTextColor(Color.parseColor(userConfig.getFontColor()));
        }
        HYLSharePreferences.clearCache(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hylhandler=(OnHYLWebHandler)activity;
    }


}
