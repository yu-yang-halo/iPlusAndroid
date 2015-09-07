package cn.lztech.newiplus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.elnet.andrmb.elconnector.WSConnector;
import cn.lztech.ProgressHUD;
import cn.lztech.RegexUtils;
import cn.lztech.adapter.DialogAdapter;
import cn.lztech.bean.AppTagGson;
import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.cache.HYLSharePreferences;

/**
 * Created by Administrator on 2015/7/22.
 */
public class HYLSettingFragment extends HeaderFragment{
    EditText serveripEdit;
    TextView customResStatusTextView;
    TextView systemResStatusTextView;
    Button   updateCustomResButton;
    Button   resetSystemResButton;
    TextView sysVersionTextView;
    Activity mcontext;
    ProgressHUD mProgressHUD;
    OnHYLWebHandler hylhandler;
    @Override
    protected void initHeaderView(View view) {
        navigationBar= (RelativeLayout) view.findViewById(R.id.navigationBar);
        rightBtn= (Button) view.findViewById(R.id.rightBtn);
        leftBtn= (Button) view.findViewById(R.id.leftBtn);
        titleText= (TextView) view.findViewById(R.id.titleText);

        titleText.setText(this.getString(R.string.app_settings));

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.hylsettings,container,false);
        initHeaderView(view);
        mcontext= this.getActivity();
        serveripEdit= (EditText) view.findViewById(R.id.serverIP);
        customResStatusTextView=(TextView)view.findViewById(R.id.customresstatus);
        systemResStatusTextView=(TextView)view.findViewById(R.id.systemresstatus);
        updateCustomResButton=(Button)view.findViewById(R.id.updateCustomRes);
        resetSystemResButton=(Button)view.findViewById(R.id.resetSystemRes);
        sysVersionTextView=(TextView)view.findViewById(R.id.sysVersion);
        serveripEdit.setText(WSConnector.getInstance().getIP1());

        this.updateUsingStatusInfo();

        updateCustomResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppTagGson appTagGson=HYLSharePreferences.getAppTagJSON(HYLSettingFragment.this.getActivity());

                List<AppTagGson.AppTagInfo> appTagInfos=appTagGson.getTagList();
                if(appTagInfos!=null&&appTagInfos.size()>0){
                    showPlusDialog(appTagInfos);
                }else{
                    Toast.makeText(HYLSettingFragment.this.getActivity(),"暂时还没有发布的应用",Toast.LENGTH_LONG).show();
                }

            }
        });

        resetSystemResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HYLResourceUtils.resetToSystemResource(mcontext);
                updateUsingStatusInfo();
            }
        });


        sysVersionTextView.setText(getVersion());

        serveripEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTextChanged", "s :" + s + " start:" + start + " count:" + count + " after:" + after);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged", "s :" + s + " start:" + start + " count:" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged", s.toString());

                if (RegexUtils.isIPAddress(s.toString())) {
                    HYLSharePreferences.cacheServerIP(getActivity(), s.toString());
                }

            }
        });


        return view;
    }
    private  void  updateUsingStatusInfo(){
        if(HYLResourceUtils.isUseCustomResource(mcontext)){
            String appTag=HYLSharePreferences.getCurrentAppTag(mcontext, HYLSharePreferences.getDownloadDirName(mcontext));
            if(appTag==null){
                customResStatusTextView.setText("");
            }else{
                customResStatusTextView.setText(appTag);
            }

            systemResStatusTextView.setText(mcontext.getString(R.string.text_no_use));
        }else{
            customResStatusTextView.setText(mcontext.getString(R.string.text_no_use));
            systemResStatusTextView.setText(mcontext.getString(R.string.text_using));
        }
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = mcontext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mcontext.getPackageName(), 0);
            String version = info.versionName;
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hylhandler=(OnHYLWebHandler)activity;
    }

    private void showPlusDialog(final List<AppTagGson.AppTagInfo> appTagInfos){


        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {

            }
        };
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                dialog.dismiss();
                downloadApp(appTagInfos.get(position-1).getUserName());
            }
        };

        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogPlus dialog) {
                //  Toast.makeText(HYLSettingFragment.this.getActivity(),"onDismiss",Toast.LENGTH_LONG).show();
            }
        };

        OnCancelListener cancelListener = new OnCancelListener() {
            @Override
            public void onCancel(DialogPlus dialog) {
                //Toast.makeText(HYLSettingFragment.this.getActivity(),"onCancel",Toast.LENGTH_LONG).show();
            }
        };



        DialogAdapter dialogAdapter=new DialogAdapter(appTagInfos,HYLSettingFragment.this.getActivity());

        final DialogPlus dialog = DialogPlus.newDialog(HYLSettingFragment.this.getActivity())
                .setContentHolder(new ListHolder())
                .setHeader(R.layout.dialog_title)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setAdapter(dialogAdapter)
                .setOnClickListener(clickListener)
                .setOnItemClickListener(itemClickListener)
                .setOnDismissListener(dismissListener)
                .setOnCancelListener(cancelListener)
                .setExpanded(false)
                .create();
        dialog.show();
    }
    private void downloadApp(final String appName){
            mProgressHUD = ProgressHUD.show(mcontext, mcontext.getString(R.string.downloading), true, true, null);
            HYLResourceUtils.startDownloadUI(mcontext, appName,new HYLResourceUtils.HYLResourceUtilsCallback(){

                @Override
                public void onFinishedDownload(boolean issuc) {
                    mProgressHUD.dismiss();
                    if(issuc){
                        Toast.makeText(mcontext,"下载成功",Toast.LENGTH_LONG).show();
                        HYLSharePreferences.cacheDownloadDirName(mcontext, appName);
                        updateUsingStatusInfo();
                    }else{
                        Toast.makeText(mcontext,"资源下载失败",Toast.LENGTH_LONG).show();
                    }

                }

            });
    }

}
