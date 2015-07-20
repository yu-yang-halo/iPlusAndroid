package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/17.
 */
public class LoginFragment extends Fragment {
    private  OnLoginHandler loginHandler;
    private  WebView webView;
    private  SwipeRefreshLayout  mSwipeLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login,container,false);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        webView=(WebView) view.findViewById(R.id.webview);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //����ˢ��ҳ��
                webView.loadUrl(webView.getUrl());
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);




        webView.loadUrl("file:///android_asset/ui/index.html");
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);//֧��js
        setting.setDefaultTextEncodingName("GBK");//�����ַ�����
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//���������Ϊ0ָ��������ռ�ÿռ䣬ֱ�Ӹ�������ҳ��

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
                if(result.isSuc){

                }
                if(loginHandler!=null){
                    loginHandler.canLogin(result.isSuc);
                }


            }
        });
        webView.addJavascriptInterface(JSContext, "jna");

        return view;
    }

    public  interface  OnLoginHandler{
        public void canLogin(boolean iscanlogin);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loginHandler=(OnLoginHandler)activity;
    }
}
