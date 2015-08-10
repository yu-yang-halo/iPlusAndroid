package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
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

import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshWebView;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/20.
 */
public class DeviceInfoFragment extends HeaderFragment {
    OnHYLWebHandler devConfigHandler;
    Bundle deviceInfoBundle;
    @Override
    public void onStart() {
        super.onStart();
        //devConfigHandler.doSomethingAtCuttentPage(HYLPage.HYL_PAGE_DEVICE_INFO,deviceInfoBundle);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        devConfigHandler=(OnHYLWebHandler)activity;
    }

    @Override
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        rightBtn= (Button) view.findViewById(R.id.rightBtn);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        titleText= (TextView) view.findViewById(R.id.titleText);
        rightBtn.setText("设置");

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceInfoBundle!=null){
                    devConfigHandler.toDeviceConfig(deviceInfoBundle);
                }
            }
        });
    }

    private void initPullRefreshView(View view){
        mPullWebView = (PullToRefreshWebView) view.findViewById(R.id.webview);//new PullToRefreshWebView(this);
        webView = mPullWebView.getRefreshableView();

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                mPullWebView.onPullDownRefreshComplete();
                setLastUpdateTime();
            }
        });
        mPullWebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<WebView> refreshView) {
                webView.reload();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<WebView> refreshView) {
            }
        });

        setLastUpdateTime();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.devicedetail,container,false);
        initHeaderView(view);

        initPullRefreshView(view);

        String infoPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/device.html";

        System.out.println("infoPath " + infoPath);

        webView.loadUrl(infoPath);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mPullWebView.onPullDownRefreshComplete();
                }
            }
        });


        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDefaultTextEncodingName("GBK");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        final HYLJSContext JSContext=new HYLJSContext(this.getActivity(),webView);
        JSContext.needBundle=this.getArguments();


        JSContext.setCurrentHandler(new HYLJSContext.HYLJNAHandler() {
            @Override
            public void onSimpleCallback(HYLJSContext.JNAResult result) {

            }
            @Override
            public void onSaveBundle(Bundle bundle) {
                deviceInfoBundle=bundle;
                if(deviceInfoBundle!=null){
                    titleText.setText(deviceInfoBundle.getString(HYLJSContext.key_deviceName));
                }

            }
            @Override
            public void onRefreshDevice() {
                JSContext.mobile_requestDeviceInfo();
            }
        });
        webView.addJavascriptInterface(JSContext, "jna");





        return view;
    }

}
