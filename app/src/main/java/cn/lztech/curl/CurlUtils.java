package cn.lztech.curl;
import static cn.lztech.curl.Curl.*;
/**
 * Created by Administrator on 2015/7/27.
 */
public class CurlUtils {
    public static boolean configWifi(String ssid,String password){
        int curl=curl_init();
        String request="http://192.168.4.1/config?command=wifi";
        String jsonData="{\"Request\":{\"Station\":{\"Connect_Station\":{\"ssid\":\""+ssid+"\",\"password\":\""+password+"\"}}}}";
        System.out.println("json data:: "+jsonData);

        curl_setopt(curl,CURLOPT_URL,request);
        curl_setopt(curl,CURLOPT_POSTFIELDS,jsonData);
        curl_setopt(curl,CURLOPT_POST,1);
        boolean isPerformSUCCESS=curl_perform(curl);
        curl_cleanup(curl);
        return isPerformSUCCESS;
    }

}


