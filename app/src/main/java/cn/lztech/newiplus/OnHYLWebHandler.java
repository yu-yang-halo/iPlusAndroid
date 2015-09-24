package cn.lztech.newiplus;

import android.os.Bundle;

import cn.elnet.andrmb.elconnector.DeviceObject;

/**
 * Created by Administrator on 2015/7/20.
 */
public interface OnHYLWebHandler {
    public void toDeviceList(boolean iscanlogin);
    public void toDeviceInfo(Bundle bundle);
    public void toDeviceConfig(Bundle bundle);
    public void toWifiConfig();
    public void toAppSettings();
    public void toRegister();
    public void toManagerDevice();
    public void toFindPassPage();


}
enum HYLPage{
    HYL_PAGE_LOGIN,
    HYL_PAGE_DEVICE_LIST,
    HYL_PAGE_DEVICE_INFO,
    HYL_PAGE_DEVICE_CONFIG,
    HYL_PAGE_WIFI_DEVICE_CONFIG,
    HYL_PAGE_APP_SYS_CONFIG,
}