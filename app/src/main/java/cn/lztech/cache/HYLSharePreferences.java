package cn.lztech.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import cn.elnet.andrmb.elconnector.ClassField;
import cn.elnet.andrmb.elconnector.ClassObject;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;
import cn.lztech.bean.AppTagGson;

/**
 * Created by Administrator on 2015/7/17.
 */
public class HYLSharePreferences {
    private final  static  String preference_key="cn.lztech.iPlus.share_key";
    private final  static  String class_cache_key="cn.lztech.iPlus.class_cache_key";

    private final  static  String userbname_key="cn.lztech.iPlus.username_key";
    private final  static  String password_key="cn.lztech.iPlus.password_key";
    private final  static  String downloaduiresource_key="cn.lztech.iPlus.downloaduiresource_key";

    private final  static  String wifissid_key="cn.lztech.iPlus.wifissid_key";
    private final  static  String server_IP_key="cn.lztech.iPlus.server_IP_key";

    private final  static  String app_Tag_JSON_key="cn.lztech.iPlus.app_Tag_JSON_key";

    public static void cacheAppTagJson(Context ctx,String appTagJSON){
        SharedPreferences sharedPref = ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(app_Tag_JSON_key,appTagJSON);
        editor.commit();
    }

    public static String getCurrentAppTag(Context ctx,String userName){
        List<AppTagGson.AppTagInfo> appTagInfos= getAppTagJSON(ctx).getTagList();
        String appTag = null;
        if (appTagInfos==null){
            return null;
        }
        for(AppTagGson.AppTagInfo appInfo :appTagInfos){
            if(appInfo.getUserName().equals(userName)){
                appTag=appInfo.getAppTag();
                break;
            }
        }
        return appTag;
    }

    public  static AppTagGson getAppTagJSON(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        String apptagString=sharedPreferences.getString(app_Tag_JSON_key, null);
         if(apptagString==null){
            return null;
        }

        Gson gson=new Gson();
        AppTagGson appTagGson=gson.fromJson(apptagString, AppTagGson.class);
        return appTagGson;
    }


    public static void  cacheServerIP(Context ctx,String serverIP){
        SharedPreferences sharedPref = ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(server_IP_key,serverIP);
        editor.commit();

        WSConnector.getInstance(serverIP,"8080",false);
    }
    public static  String getServerIP(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        String serverIP=sharedPreferences.getString(server_IP_key, null);
        return serverIP;
    }


    public static void  setWIFISSID(Context ctx,String ssid){
        SharedPreferences sharedPref = ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(wifissid_key,ssid);
        editor.commit();
    }
    public static String  getWIFISSID(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        String ssid=sharedPreferences.getString(wifissid_key, null);
        return ssid;
    }

    public static void  cacheDownloadDirName(Context ctx,String dirName){
        SharedPreferences sharedPref = ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(downloaduiresource_key,dirName);
        editor.commit();
    }
    public static String  getDownloadDirName(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        String dirname=sharedPreferences.getString(downloaduiresource_key, null);
        return dirname;
    }

    public static  void  cacheUsernamePassword(Context ctx, String username,String password){
        SharedPreferences sharedPref = ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userbname_key,username);
        editor.putString(password_key,password);
        editor.commit();
    }
    public static void clearUsernamePassword(Context ctx){
        SharedPreferences sharedPref = ctx.getSharedPreferences(preference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userbname_key,null);
        editor.putString(password_key,null);
        editor.commit();
    }

    public static String[] getUsernamePassword(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(preference_key,Context.MODE_PRIVATE);
        String username=sharedPreferences.getString(userbname_key, null);
        String password=sharedPreferences.getString(password_key,null);
        if(username==null||password==null){
            return null;
        }else {
            return new String[]{username,password};
        }
    }
    private static void loadFieldDisableYN(HYLUserResourceConfig.HYLFieldList  hylFieldList,ClassObject clsObj){
      if(hylFieldList==null){
          return;
      }
      for (int i=0;i<clsObj.getClassFeilds().size();i++){

          for (HYLUserResourceConfig.HYLField  hylField :hylFieldList.getFileList()){

              if(((ClassField)clsObj.getClassFeilds().get(i)).getFieldId()==hylField.getFieldId()){
                  ((ClassField)clsObj.getClassFeilds().get(i)).setDisableYN(hylField.getDisableYN()==0);
              }else{
                  ((ClassField)clsObj.getClassFeilds().get(i)).setDisableYN(false);
              }

          }

      }

    }

    public static void clearCache(Context ctx){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(class_cache_key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static ClassObject classObjectFromCache(Context ctx,Integer classId,HYLUserResourceConfig.HYLFieldList  hylFieldList){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(class_cache_key,Context.MODE_PRIVATE);
        String  clsJSON=sharedPreferences.getString(classId.toString(), null);
        ClassObject clsobj=null;
        Gson gson=new Gson();
        Type clstype = new TypeToken<ClassObject<ClassField>>(){}.getType();

        if(clsJSON==null){
            try {
                clsobj = WSConnector.getInstance().getClass(classId);

                if(clsobj!=null) {
                    loadFieldDisableYN(hylFieldList,clsobj);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(classId.toString(), gson.toJson(clsobj,clstype));
                    editor.commit();

                }
            } catch (WSException e) {
                e.printStackTrace();
            }
        }else{

            clsobj=gson.fromJson(clsJSON,clstype);
        }

        return clsobj;
    }

}
