package cn.lztech.newiplus;

import android.os.AsyncTask;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import cn.elnet.andrmb.elconnector.CcsClientInfo;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;
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
    private RadioButton radioButton0;
    private RadioButton radioButton1;

    private int currentPage=0;
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
                   if(result==null){
                       loadRadioButtons();
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
    public class DataRequestTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                List<CcsClientInfo> ccsClientInfos=WSConnector.getInstance().getCcsListByUser();
                Gson gson=new Gson();
                String ccsClientInfoStr=gson.toJson(ccsClientInfos);
                Log.e("ccsClientInfoStr",ccsClientInfoStr);
                return ccsClientInfoStr;
            } catch (WSException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null&&!"".equals(s.trim())){

            }
        }
    }
    @Override
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        segmentedGroup=(SegmentedGroup)view.findViewById(R.id.segmented);
        radioButton0= (RadioButton) view.findViewById(R.id.button01);
        radioButton1= (RadioButton) view.findViewById(R.id.button02);

        loadRadioButtons();

        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button01:
                        currentPage=0;
                        webView.loadUrl("javascript:hyl_switchPage(0)");
                        break;
                    case R.id.button02:
                        currentPage=1;
                        webView.loadUrl("javascript:hyl_switchPage(1)");
                        break;
                    default:
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
    private void  loadRadioButtons(){
        if(radioButton0!=null&&radioButton1!=null){
            if(currentPage==0){
                radioButton0.setChecked(true);
                radioButton1.setChecked(false);
            }else{
                radioButton0.setChecked(false);
                radioButton1.setChecked(true);
            }
            webView.loadUrl("javascript:hyl_switchPage("+currentPage+")");
        }

    }
}
