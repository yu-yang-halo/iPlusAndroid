package cn.lztech.jscontext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.elnet.andrmb.elconnector.ErrorCode;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;
import cn.elnet.andrmb.elconnector.util.MD5Generator;
import cn.elnet.andrmb.elconnector.util.Util;
import cn.lztech.cn.lztech.cache.HYLSharePreferences;
import cn.lztech.newiplus.R;

/**
 * Created by Administrator on 2015/7/16.
 */
public class HYLJSContext {
    public Context mContext;
    private HYLJNAHandler mhandler;
    private WebView mwebView;

    public HYLJSContext(Context context,WebView webView){
        this.mContext = context;
        this.mwebView=webView;
    }

    public void setCurrentHandler(HYLJNAHandler handler){
        mhandler=handler;
    }

    private Handler mhander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://初始化记住的用户名和密码
                    String[]  userpass=HYLSharePreferences.getUsernamePassword(mContext);
                    if(userpass!=null){
                        mwebView.loadUrl("javascript: hyl_setUsernamePassToView('" + userpass[0] + "','" + userpass[1] + "')");
                    }

                    break;
                default:
                    break;
            }

        }
    };

    @JavascriptInterface
    public void mobile_login(String  username,String password){

        if("".equals(username.trim())||"".equals(password.trim())){
            Toast.makeText(mContext,mContext.getString(R.string.err_username_pass),Toast.LENGTH_SHORT).show();
        }else{
            HYLSharePreferences.cacheUsernamePassword(mContext,username,password);
            new RequestNetTask(RequestType.REQUEST_TYPE_LOGIN,mhandler).execute(username, password);
        }

    }

    @JavascriptInterface
    public void mobile_loadDefaultUsernamePass(){
        Message msg = new Message();
        msg.what=0;
        mhander.sendMessage(msg);

    }





   public interface HYLJNAHandler {
        public void  onSimpleCallback(JNAResult result);
    }
    public class JNAResult {
        public  Boolean isSuc;
        public  String  jsonString;
    }

    public class  RequestNetTask extends AsyncTask<String,Integer,JNAResult>{
        private  RequestType requestType;
        private HYLJNAHandler _handler;
        private WSConnector wsconnector=WSConnector.getInstance();
        public  RequestNetTask(RequestType type,HYLJNAHandler handler) {
            requestType=type;
            _handler=handler;
        }
        @Override
        protected JNAResult doInBackground(String[] params) {
            JNAResult result=new JNAResult();
            if(requestType==RequestType.REQUEST_TYPE_LOGIN) {
                Boolean isSuc=true;
                try {
                    if(params.length>=2){
                        wsconnector.appUserLogin(params[0],params[1]);
                    }else{
                        isSuc=false;
                        result.jsonString="no writing username or password";
                    }

                } catch (WSException e) {
                    isSuc=false;
                }
                result.isSuc=isSuc;
            }

            return result;
        }

        @Override
        protected void onPostExecute(JNAResult o) {
            super.onPostExecute(o);
            _handler.onSimpleCallback(o);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }
}



