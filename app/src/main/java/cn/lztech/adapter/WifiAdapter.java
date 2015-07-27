package cn.lztech.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.lztech.newiplus.R;

/**
 * Created by Administrator on 2015/7/27.
 */
public class WifiAdapter extends BaseAdapter {
    List<ScanResult> wifiList;
    Context ctx;
    public  WifiAdapter(List<ScanResult> wifiList,Context ctx){
        this.wifiList=wifiList;
        this.ctx=ctx;
    }
    @Override
    public int getCount() {

        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        ScanResult result=wifiList.get(position);
        return result.BSSID;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(ctx).inflate(R.layout.wifiitem,parent,false);
        }
        TextView  textView=(TextView)convertView.findViewById(R.id.textView);

        textView.setText(wifiList.get(position).BSSID);



        return convertView;
    }
}
