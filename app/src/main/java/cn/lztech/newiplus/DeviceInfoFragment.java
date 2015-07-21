package cn.lztech.newiplus;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/20.
 */
public class DeviceInfoFragment extends Fragment {
    SwipeRefreshLayout mSwipeLayout;
    WebView webView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_deviceconfig, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.devicedetail,container,false);
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

        String infoPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/device.html";

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
                DeviceInfoFragment.this.getActivity().getActionBar().setTitle(bundle.getString(HYLJSContext.key_deviceName));
            }
        });
        webView.addJavascriptInterface(JSContext, "jna");





        return view;
    }
}
