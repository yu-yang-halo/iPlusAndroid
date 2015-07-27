package cn.lztech.newiplus;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import cn.lztech.adapter.WifiAdapter;

/**
 * Created by Administrator on 2015/7/22.
 */
public class HYLWifiConfigFragment extends Fragment {
    ListView listView=null;
    private WifiManager wifiManager;
    List<ScanResult> wifidatas;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.hylwificonfig,container,false);
        this.getActivity().getActionBar().setTitle(this.getActivity().getString(R.string.wifi_settings));
        listView= (ListView) view.findViewById(R.id.listView);
        wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        openWifi();
        wifidatas = wifiManager.getScanResults();

        WifiAdapter adapter=new WifiAdapter(wifidatas,this.getActivity());

        listView.setAdapter(adapter);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
    /**
     *  打开WIFI
     */
    private void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

    }
}
