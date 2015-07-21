package cn.lztech.jscontext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.elnet.andrmb.elconnector.ClassField;
import cn.elnet.andrmb.elconnector.ClassObject;
import cn.elnet.andrmb.elconnector.DeviceObject;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.newiplus.R;

/**
 * Created by Administrator on 2015/7/16.
 */
public class HYLJSContext {
    public final static  String key_objectId="objectId_KEY";
    public final static  String key_classId="classId_KEY";
    public final static  String key_deviceName="deviceName_KEY";
    public final static  String key_currentDeviceObjectJSON="currentDeviceObject_KEY";
    public Context mContext;
    private HYLJNAHandler mhylhandler;
    private WebView mwebView;
    private Gson gson=new Gson();
    public  Bundle needBundle;
    private DeviceObject deviceObject;

    public HYLJSContext(Context context, WebView webView) {
        this.mContext = context;
        this.mwebView = webView;
    }

    public Bundle getNeedBundle() {
        if(needBundle==null){
            return new Bundle();
        }
        return needBundle;
    }

    public void setCurrentHandler(HYLJNAHandler handler) {
        mhylhandler = handler;
    }

    private Handler mhander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String[] userpass = HYLSharePreferences.getUsernamePassword(mContext);
                    if (userpass != null) {
                        mwebView.loadUrl("javascript: hyl_setUsernamePassToView('" + userpass[0] + "','" + userpass[1] + "')");
                    }

                    break;
                case 1:
                    ClassObject clsobj= HYLSharePreferences.classObjectFromCache(mContext, msg.getData().getInt(key_classId));
                    Map<Integer,List<ClassField>> clsMap=new HashMap<Integer,List<ClassField>>();
                    clsMap.put(clsobj.getClassId(),clsobj.getClassFeilds());
                    String  clsJSON=gson.toJson(clsMap);
                    String  devJSON=msg.getData().getString(key_currentDeviceObjectJSON);
                    System.out.println("devJSON:"+devJSON +"\n  clsJSON:"+clsJSON);
                    mhylhandler.onSaveBundle(msg.getData());

                    mwebView.loadUrl("javascript:loadDeviceInfoToHtml("+devJSON+","+clsJSON+")");
                    mhylhandler.onSaveBundle(msg.getData());
                    break;
                default:
                    break;
            }

        }
    };
    @JavascriptInterface
    public void mobile_setFieldCmd(final String fieldValue, final String fieldId, final String objectId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WSConnector.getInstance().setFieldValue(Integer.parseInt(objectId),Integer.parseInt(fieldId),fieldValue,true);
                    getNeedBundle().putInt(key_objectId, Integer.parseInt(objectId));
                    mobile_requestDeviceInfo();
                } catch (WSException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @JavascriptInterface
    public  void  mobile_requestDeviceInfo(){
         if(needBundle!=null){
           final int objectId= needBundle.getInt(key_objectId);
             if(objectId>0){
                 new Thread(){
                     @Override
                     public void run() {
                         try {
                             deviceObject = WSConnector.getInstance().getObjectValue(objectId);

                             if(deviceObject!=null){

                                 Message msg=new Message();
                                 msg.what=1;
                                 Bundle bundle=new Bundle();
                                 bundle.putInt(key_objectId,deviceObject.getObjectId());
                                 bundle.putInt(key_classId,deviceObject.getClassId());
                                 bundle.putString(key_currentDeviceObjectJSON, gson.toJson(deviceObject));
                                 bundle.putString(key_deviceName,deviceObject.getName());
                                 msg.setData(bundle);
                                 mhander.sendMessage(msg);
                             }
                         } catch (WSException e) {
                             e.printStackTrace();
                         }

                     }
                 }.start();
             }

         }
    }

    @JavascriptInterface
    public void mobile_toDetailPage(String objectId) {
        Bundle bundle=new Bundle();
        bundle.putInt(key_objectId,Integer.parseInt(objectId));
        mhylhandler.onSaveBundle(bundle);
    }




    @JavascriptInterface
    public void mobile_login(String  username,String password){

        if("".equals(username.trim())||"".equals(password.trim())){
            Toast.makeText(mContext,mContext.getString(R.string.err_username_pass),Toast.LENGTH_SHORT).show();
        }else{
            HYLSharePreferences.cacheUsernamePassword(mContext,username,password);
            new RequestNetTask(RequestType.REQUEST_TYPE_LOGIN,mhylhandler).execute(username, password);
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
        public void  onSaveBundle(Bundle bundle);
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



