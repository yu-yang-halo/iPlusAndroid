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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshWebView;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/22.
 */
public class DeviceConfigFragment extends HeaderFragment {
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
    @Override
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        rightBtn= (Button) view.findViewById(R.id.rightBtn);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        titleText= (TextView) view.findViewById(R.id.titleText);
        titleText.setText("设置");

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initPullRefreshView(View view){
        mPullWebView = (PullToRefreshWebView) view.findViewById(R.id.webview_config);//new PullToRefreshWebView(this);
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
        View view=inflater.inflate(R.layout.deviceconfig,container,false);
        initHeaderView(view);

        initPullRefreshView(view);


        String infoPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/deviceConfig.html";

        System.out.println("infoPath " + infoPath);

        webView.loadUrl(infoPath);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mPullWebView.onPullDownRefreshComplete();
                    Log.i("progress ", "" + newProgress);
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
            }
            @Override
            public void onRefreshDevice() {

            }
        });
        webView.addJavascriptInterface(JSContext, "jna");





        return view;
    }


}
