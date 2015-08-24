package cn.lztech.newiplus;

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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.jscontext.HYLJSContext;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Administrator on 2015/8/24.
 */
public class HYLManagerFragment extends HeaderFragment {
    private  OnHYLWebHandler hylhandler;
    private SwipeRefreshLayout mSwipeLayout;
    private SegmentedGroup segmentedGroup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.manager,container,false);
        webView=(WebView) view.findViewById(R.id.webview);




        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
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


        String indexPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/manager.html";

        System.out.println("indexPath " + indexPath);

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
                } else {
                    if (!mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(true);
                    }
                }
            }

        });

        initHeaderView(view);


        HYLJSContext JSContext=new HYLJSContext(this.getActivity(),webView);
        JSContext.setCurrentHandler(new HYLJSContext.HYLJNAHandler() {
            @Override
            public void onSimpleCallback(HYLJSContext.JNAResult result) {

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
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        segmentedGroup=(SegmentedGroup)view.findViewById(R.id.segmented);
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button01:
                        webView.loadUrl("javascript:hyl_switchPage(0)");
                        break;
                    case R.id.button02:
                        webView.loadUrl("javascript:hyl_switchPage(1)");
                        break;
                    default:
                        // Nothing to do
                        break;
                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


    }
}
