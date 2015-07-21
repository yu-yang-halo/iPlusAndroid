package cn.lztech.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import cn.elnet.andrmb.elconnector.ClassField;
import cn.elnet.andrmb.elconnector.ClassObject;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;

/**
 * Created by Administrator on 2015/7/17.
 */
public class HYLSharePreferences {
    private final  static  String preference_key="cn.lztech.iPlus.share_key";
    private final  static  String userbname_key="cn.lztech.iPlus.username_key";
    private final  static  String password_key="cn.lztech.iPlus.password_key";
    private final  static  String downloaduiresource_key="downloaduiresource_key";

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
    public static ClassObject classObjectFromCache(Context ctx,Integer classId){
        SharedPreferences sharedPreferences=ctx.getSharedPreferences(preference_key,Context.MODE_PRIVATE);
        String  clsJSON=sharedPreferences.getString(classId.toString(), null);
        ClassObject clsobj=null;
        Gson gson=new Gson();
        Type clstype = new TypeToken<ClassObject<ClassField>>(){}.getType();

        if(clsJSON==null){
            try {
                clsobj = WSConnector.getInstance().getClass(classId);
                if(clsobj!=null) {
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
