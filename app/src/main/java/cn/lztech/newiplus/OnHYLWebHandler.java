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
}
