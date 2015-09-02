package cn.lztech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lztech.newiplus.R;

/**
 * Created by Administrator on 2015/9/1.
 */
public class DialogAdapter extends BaseAdapter {
    List<String> datas=null;
    Context ctx;
    public DialogAdapter(List<String> datas,Context ctx){
        this.datas=datas;
        this.ctx=ctx;
    }
    @Override
    public int getCount() {
        if(datas!=null){
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(datas!=null){
            return datas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.simple_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(datas!=null){
            viewHolder.textView.setText(datas.get(position));
        }
        return view;
    }
    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
