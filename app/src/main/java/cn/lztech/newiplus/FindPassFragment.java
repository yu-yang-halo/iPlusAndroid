package cn.lztech.newiplus;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.Toast;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/9/23.
 */
public class FindPassFragment extends  HeaderFragment{
    private  OnHYLWebHandler hylhandler;
    private  WebView webView;
    private  SwipeRefreshLayout  mSwipeLayout;
    private  Button backBtn;
    @Override
    protected void initHeaderView(View view) {

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.findpass, container, false);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        webView=(WebView) view.findViewById(R.id.webview_findpass);
        backBtn=(Button)view.findViewById(R.id.leftBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindPassFragment.this.getActivity().onBackPressed();
            }
        });

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


        String indexPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/findpass.html";

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
                    Log.i("progress ", "" + progress);
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

            }

            @Override
            public void onSaveBundle(Bundle bundle) {
                    Toast.makeText(FindPassFragment.this.getActivity(),bundle.get(HYLJSContext.key_phoneOrEmail).toString(),Toast.LENGTH_LONG).show();
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
