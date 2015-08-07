package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.elnet.andrmb.elconnector.ClassObject;
import cn.elnet.andrmb.elconnector.DeviceObject;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;
import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.cache.HYLUserResourceConfig;
import cn.lztech.jscontext.HYLJSContext;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2015/7/17.
 */
public class DeviceListFragment extends HeaderFragment {

    SwipeRefreshLayout mSwipeLayout;
    WebView webView;
    OnHYLWebHandler hylhandler;
    @Override
    public void onStart() {
        super.onStart();
        if(userConfig==null){
            titleText.setText("设备列表");
        }else{
            titleText.setText(userConfig.getTitle().getDevices());
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hylhandler=(OnHYLWebHandler)activity;
    }
    @Override
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        rightBtn= (Button) view.findViewById(R.id.rightBtn);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        titleText= (TextView) view.findViewById(R.id.titleText);
        rightBtn.setText("配置");

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getActivity().onBackPressed();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hylhandler.toWifiConfig();
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.devicedetail,container,false);
        initHeaderView(view);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RefreshDeviceListTask(DeviceListFragment.this.getActivity(), webView, mSwipeLayout).execute((String[]) null);
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);


        webView=(WebView) view.findViewById(R.id.webview);



        String detailPath= HYLResourceUtils.rootPath(this.getActivity())+"ui/devices.html";

        System.out.println("detailPath " + detailPath);

        webView.loadUrl(detailPath);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i("onProgressChanged",newProgress+"---");
            }
        });


        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDefaultTextEncodingName("GBK");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        final HYLJSContext JSContext=new HYLJSContext(this.getActivity(),webView);
        JSContext.setCurrentHandler(new HYLJSContext.HYLJNAHandler() {
            @Override
            public void onSimpleCallback(HYLJSContext.JNAResult result) {
                if (result.isSuc) {

                }
            }

            @Override
            public void onSaveBundle(Bundle bundle) {
                hylhandler.toDeviceInfo(bundle);
            }
            @Override
            public void onRefreshDevice() {

            }
        });
        webView.addJavascriptInterface(JSContext, "jna");

        new RefreshDeviceListTask(DeviceListFragment.this.getActivity(),webView,mSwipeLayout).execute((String[]) null);



        return view;
    }




    class RefreshDeviceListTask extends AsyncTask<String,String,String[]>{
        Context mcontext;
        WebView mwebview;
        SwipeRefreshLayout mSwipeLayout;

        RefreshDeviceListTask(Context ctx,WebView webView,SwipeRefreshLayout swipeLayout){
              this.mcontext=ctx;
              this.mwebview=webView;
              this.mSwipeLayout=swipeLayout;
        }
        @Override
        protected String[] doInBackground(String... params) {
             String alldevobjJSON=null;
             String allclassobjJSON=null;
             String allclassiconsJSON=null;
            WSConnector wsConnector = WSConnector.getInstance();
            try {
                Map<Integer, DeviceObject> devsmap=wsConnector.getObjectListAndFieldsByUser();
                Collection<DeviceObject> devlist= devsmap.values();
                Gson gson=new Gson();
                alldevobjJSON= gson.toJson(devlist);
                Log.i("alldevobjJSON",alldevobjJSON);

                Map<Integer,List> allClassObjs=new HashMap<Integer,List>();
                Map<Integer,String> allClassIcons=new HashMap<Integer,String>();

                for(DeviceObject dev : devlist){
                    ClassObject clsObj=HYLSharePreferences.classObjectFromCache(DeviceListFragment.this.getActivity(), dev.getClassId());

                    allClassObjs.put(clsObj.getClassId(),clsObj.getClassFeilds());

                    if(clsObj.getIcon()!=null){
                        allClassIcons.put(clsObj.getClassId(),clsObj.getIcon());
                    }

                }
                allclassobjJSON=gson.toJson(allClassObjs);
                allclassiconsJSON=gson.toJson(allClassIcons);


            } catch (WSException e) {
                e.printStackTrace();
            }


            return new String[]{alldevobjJSON,allclassobjJSON,allclassiconsJSON};
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            mSwipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeLayout.setRefreshing(false);
                }
            });

            mwebview.loadUrl("javascript:hyl_loadDevicesData(" + s[0] + "," + s[1] + "," + s[2] + ")");

            Log.i("onPostExecute"," end ..");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("onPreExecute", "  start..");
            mSwipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (!mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(true);
                    }
                }
            });


        }
    }
}
