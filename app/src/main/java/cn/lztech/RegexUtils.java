package cn.lztech;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/7/23.
 */
public class RegexUtils {
    public  static  boolean isIPAddress(String ipaddr){
        /**
         * 判断IP格式和范围
         */
        String rexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(ipaddr);


        return mat.matches();

    }
    public static boolean isEamil(String email){
        /**
         * 判断是否是邮箱
         */
        String rexp="^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
        Pattern pattern = Pattern.compile(rexp);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
