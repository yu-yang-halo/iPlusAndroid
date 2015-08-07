package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lztech.cache.HYLUserResourceConfig;

/**
 * Created by Administrator on 2015/8/7.
 */
public abstract class HeaderFragment extends Fragment {
   protected  RelativeLayout navigationBar;
   protected  Button rightBtn;
   protected  Button leftBtn;
   protected  TextView titleText;
    HYLUserResourceConfig.UserConfig userConfig;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onStart() {
        super.onStart();
        userConfig=HYLUserResourceConfig.loadUserConfig(getActivity());
    }

    protected abstract void initHeaderView(View view);

}
