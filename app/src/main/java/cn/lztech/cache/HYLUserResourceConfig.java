package cn.lztech.cache;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * Created by Administrator on 2015/7/27.
 */
public class HYLUserResourceConfig {

     public  static  UserConfig loadUserConfig(Context ctx){
         if(!HYLResourceUtils.isUseCustomResource(ctx)){
             return null;
         }
         String configName="config.json";
         String defaultConfigName="configdefault.json";
         String configPath= HYLResourceUtils.userCustomUIResPath(ctx)+"/config/"+configName;
         Gson gson=new Gson();
         try {
             Reader reader=new FileReader(new File(configPath));
             UserConfig userConfig=gson.fromJson(reader, UserConfig.class);
             return userConfig;
         } catch (FileNotFoundException e) {
             Log.e("loadUserConfig","FileNotFoundException");
         }

         return null;
     }



    public class UserConfig{
        private Title title;
        private String barColor;
        private String fontColor;
        private short fontSize;
        private String version;

        public Title getTitle() {
            return title;
        }

        public void setTitle(Title title) {
            this.title = title;
        }

        public String getBarColor() {
            return barColor;
        }

        public void setBarColor(String barColor) {
            this.barColor = barColor;
        }

        public String getFontColor() {
            return fontColor;
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }

        public short getFontSize() {
            return fontSize;
        }

        public void setFontSize(short fontSize) {
            this.fontSize = fontSize;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
    public class Title{
        private  String login;
        private  String devices;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getDevices() {
            return devices;
        }

        public void setDevices(String devices) {
            this.devices = devices;
        }
    }
}
