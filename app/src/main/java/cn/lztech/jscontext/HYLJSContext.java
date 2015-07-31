package cn.lztech.jscontext;

import android.content.Context;
import android.content.DialogInterface;
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
import cn.lztech.ProgressHUD;
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
    public final static  String key_errormessageKEY="key_errormessageKEY";
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

                    mwebView.loadUrl("javascript:loadDeviceInfoToHtml("+devJSON+","+clsJSON+")");
                    mhylhandler.onSaveBundle(msg.getData());
                    break;
                case 2:
                    mhylhandler.onRefreshDevice();
                    break;
                case  10000:
                    Toast.makeText(mContext, msg.getData().getString(key_errormessageKEY), Toast.LENGTH_SHORT).show();
                    mhylhandler.onRefreshDevice();
                    break;
                default:
                    break;
            }

        }
    };

    @JavascriptInterface
    public void mobile_updateDeviceName(final String name, final String objectId){
        new Thread(new Runnable() {
            @Override
            public void run() {

                DeviceObject deviceObject= null;
                try {
                    deviceObject = WSConnector.getInstance().getObjectValue(Integer.parseInt(objectId));
                    deviceObject.setName(name);
                    WSConnector.getInstance().updateObject(deviceObject);
                } catch (WSException e) {
                    Message msg=new Message();
                    msg.what=10000;
                    Bundle bundle=new Bundle();
                    bundle.putString(key_errormessageKEY,e.getErrorMsg());
                    msg.setData(bundle);
                    mhander.sendMessage(msg);
                }
            }
        }).start();
    }

    @JavascriptInterface
    public void mobile_setFieldCmd(final String fieldValue, final String fieldId, final String objectId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                try {
                    WSConnector.getInstance().setFieldValue(Integer.parseInt(objectId), Integer.parseInt(fieldId), fieldValue, true);
                    getNeedBundle().putInt(key_objectId, Integer.parseInt(objectId));
                    msg.what=2;


                } catch (WSException e) {
                    msg.what=10000;
                    Bundle bundle=new Bundle();
                    bundle.putString(key_errormessageKEY,e.getErrorMsg());
                    msg.setData(bundle);

                }
                mhander.sendMessage(msg);

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
                         Message msg=new Message();
                         try {
                             deviceObject = WSConnector.getInstance().getObjectValue(objectId);

                             if(deviceObject!=null){


                                 msg.what=1;
                                 Bundle bundle=new Bundle();
                                 bundle.putInt(key_objectId,deviceObject.getObjectId());
                                 bundle.putInt(key_classId, deviceObject.getClassId());
                                 bundle.putString(key_currentDeviceObjectJSON, gson.toJson(deviceObject));
                                 bundle.putString(key_deviceName, deviceObject.getName());
                                 msg.setData(bundle);

                             }
                         } catch (WSException e) {
                             msg.what=10000;
                             Bundle bundle=new Bundle();
                             bundle.putString(key_errormessageKEY,e.getErrorMsg());
                             msg.setData(bundle);
                         }
                         mhander.sendMessage(msg);
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
        public void  onRefreshDevice();
    }
    public class JNAResult {
        public  Boolean isSuc;
        public  String  jsonString;
    }

    public class  RequestNetTask extends AsyncTask<String,Integer,JNAResult> implements DialogInterface.OnCancelListener {
        private  RequestType requestType;
        private HYLJNAHandler _handler;
        private WSConnector wsconnector=WSConnector.getInstance();
        ProgressHUD mProgressHUD;
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
                    result.jsonString=e.getErrorMsg();
                }
                result.isSuc=isSuc;
            }

            return result;
        }

        @Override
        protected void onPostExecute(JNAResult o) {
            mProgressHUD.dismiss();
            super.onPostExecute(o);
            _handler.onSimpleCallback(o);

        }

        @Override
        protected void onPreExecute() {
            mProgressHUD = ProgressHUD.show(mContext,mContext.getString(R.string.logining), true,true,this);
            super.onPreExecute();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            this.cancel(true);
            mProgressHUD.dismiss();
        }
    }
}



