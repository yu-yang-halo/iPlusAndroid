package cn.lztech.newiplus;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/7/22.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HYLSettingFragment extends Fragment{
    EditText serveripEdit;
    TextView customResStatusTextView;
    TextView systemResStatusTextView;
    Button   updateCustomResButton;
    Button   resetSystemResButton;
    TextView sysVersionTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.hylsettings,container,false);
        this.getActivity().getActionBar().setTitle(this.getActivity().getString(R.string.app_settings));

        serveripEdit= (EditText) view.findViewById(R.id.serverIP);
        customResStatusTextView=(TextView)view.findViewById(R.id.customresstatus);
        systemResStatusTextView=(TextView)view.findViewById(R.id.systemresstatus);
        updateCustomResButton=(Button)view.findViewById(R.id.updateCustomRes);
        resetSystemResButton=(Button)view.findViewById(R.id.resetSystemRes);
        sysVersionTextView=(TextView)view.findViewById(R.id.sysVersion);
        serveripEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTextChanged","s :"+s+" start:"+start+" count:"+count+" after:"+after);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged","s :"+s+" start:"+start+" count:"+count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged",s.toString());
            }
        });


        return view;
    }
}
