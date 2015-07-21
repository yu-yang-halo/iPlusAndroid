package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
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
import android.webkit.WebViewClient;
import android.widget.Toast;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;

/**
 * Created by Administrator on 2015/7/17.
 */
public class LoginFragment extends Fragment {
    private  OnHYLWebHandler loginHandler;
    private  WebView webView;
    private  SwipeRefreshLayout  mSwipeLayout;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hylactivity_login, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login,container,false);

        this.getActivity().getActionBar().setTitle("海易联");

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
                if(result.isSuc){

                }
                if(loginHandler!=null){
                    loginHandler.canLogin(result.isSuc);
                }


            }

            @Override
            public void onSaveBundle(Bundle bundle) {

            }
        });
        webView.addJavascriptInterface(JSContext, "jna");

        return view;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loginHandler=(OnHYLWebHandler)activity;
    }
}
