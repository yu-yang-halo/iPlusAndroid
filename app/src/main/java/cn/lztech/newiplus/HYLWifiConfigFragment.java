package cn.lztech.newiplus;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.List;

import cn.lztech.WifiAdmin;
import cn.lztech.adapter.WifiAdapter;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.curl.CurlUtils;

/**
 * Created by Administrator on 2015/7/22.
 */
public class HYLWifiConfigFragment extends Fragment{
    LinearLayout ssidLayout;
    LinearLayout passLayout;
    LinearLayout configLayout;
    EditText ssidEdit;
    EditText passEdit;

    Handler wifiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(HYLWifiConfigFragment.this.getActivity(),"配置失败",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    showSuccessDialog(HYLWifiConfigFragment.this.getActivity());
                    break;
                default:
                    break;
            }
        }
    };
    private void showSuccessDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("配置成功");
        builder.setMessage("您现在的网络已断开，是否重新联网？");
        builder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        toWifiSetting();
                    }
                });
        builder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        WifiAdmin wifiAdmin=new WifiAdmin(this.getActivity());

        String wifiSSID=HYLSharePreferences.getWIFISSID(this.getActivity());
        if(wifiSSID.equals(wifiAdmin.getSSID())){
            setLayoutHidden(true);
        }else{
            setLayoutHidden(false);
            ssidEdit.setText(wifiSSID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.hylwificonfig,container,false);
        this.getActivity().getActionBar().setTitle(this.getActivity().getString(R.string.wifi_settings));

        Button toWifiBtn= (Button) view.findViewById(R.id.towifiDeviceBtn);
        Button configBtn= (Button) view.findViewById(R.id.configBtn);
        ssidEdit= (EditText) view.findViewById(R.id.ssidEdit);
        passEdit= (EditText) view.findViewById(R.id.passEdit);
        ssidLayout= (LinearLayout) view.findViewById(R.id.layoutSSID);
        passLayout= (LinearLayout) view.findViewById(R.id.layoutPASS);
        configLayout= (LinearLayout) view.findViewById(R.id.layoutConfig);


        configBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ssidEdit.getText().toString().trim().equals("") && !passEdit.getText().toString().trim().equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSUC = CurlUtils.configWifi(ssidEdit.getText().toString(), passEdit.getText().toString());
                            Message msg = new Message();
                            msg.what = isSUC ? 1 : 0;
                            wifiHandler.sendMessage(msg);
                        }
                    }).start();

                } else {
                    Toast.makeText(HYLWifiConfigFragment.this.getActivity(), "WIFI名称密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toWifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 toWifiSetting();
            }
        });
        return view;
    }
    private void toWifiSetting(){
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }
    private void setLayoutHidden(boolean isHidden){
        if(isHidden){
            ssidLayout.setVisibility(View.GONE);
            passLayout.setVisibility(View.GONE);
            configLayout.setVisibility(View.GONE);
        }else{
            ssidLayout.setVisibility(View.VISIBLE);
            passLayout.setVisibility(View.VISIBLE);
            configLayout.setVisibility(View.VISIBLE);
        }


    }
}
