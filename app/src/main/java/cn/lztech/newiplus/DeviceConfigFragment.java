package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/22.
 */
public class DeviceConfigFragment extends Fragment {
    SwipeRefreshLayout mSwipeLayout;
    WebView webView;
    Bundle dataBundle;
    OnHYLWebHandler devConfigHandler;
    @Override
    public void onStart() {
        super.onStart();
       // devConfigHandler.doSomethingAtCuttentPage(HYLPage.HYL_PAGE_DEVICE_CONFIG,dataBundle);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        devConfigHandler=(OnHYLWebHandler)activity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.deviceconfig,container,false);


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

        String infoPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/deviceConfig.html";

        System.out.println("infoPath " + infoPath);

        webView.loadUrl(infoPath);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mSwipeLayout.setRefreshing(false);
                    Log.i("progress ", "" + newProgress);
                } else {
                    if (!mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(true);
                    }
                }
            }
        });


        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDefaultTextEncodingName("GBK");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        HYLJSContext JSContext=new HYLJSContext(this.getActivity(),webView);
        JSContext.needBundle=this.getArguments();


        JSContext.setCurrentHandler(new HYLJSContext.HYLJNAHandler() {
            @Override
            public void onSimpleCallback(HYLJSContext.JNAResult result) {

            }
            @Override
            public void onSaveBundle(Bundle bundle) {
                dataBundle=bundle;
                devConfigHandler.doSomethingAtCuttentPage(HYLPage.HYL_PAGE_DEVICE_CONFIG,dataBundle);
            }
            @Override
            public void onRefreshDevice() {

            }
        });
        webView.addJavascriptInterface(JSContext, "jna");





        return view;
    }
}
