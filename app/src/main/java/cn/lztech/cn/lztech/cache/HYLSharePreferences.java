package cn.lztech.cn.lztech.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import cn.elnet.andrmb.elconnector.ClassObject;
import cn.elnet.andrmb.elconnector.WSConnector;
import cn.elnet.andrmb.elconnector.WSException;
import cn.lztech.newiplus.R;

/**
 * Created by Administrator on 2015/7/17.
 */
public class HYLSharePreferences {
    private final  static  String preference_key="cn.lztech.iPlus.share_key";
    private final  static  String userbname_key="cn.lztech.iPlus.username_key";
    private final  static  String password_key="cn.lztech.iPlus.password_key";
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
        if(clsJSON==null){
            try {
                clsobj = WSConnector.getInstance().getClass(classId);
                if(clsobj!=null) {
                    sharedPreferences.edit().putString(classId.toString(), gson.toJson(clsobj));
                }
            } catch (WSException e) {
                e.printStackTrace();
            }
        }else{
            clsobj=gson.fromJson(clsJSON,ClassObject.class);
        }

        return clsobj;
    }

}
