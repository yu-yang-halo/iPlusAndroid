package cn.lztech.newiplus;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lee.pullrefresh.ui.PullToRefreshWebView;

import java.text.SimpleDateFormat;
import java.util.Date;

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


    protected WebView webView;
    protected PullToRefreshWebView mPullWebView;
    protected SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");


    protected void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullWebView.setLastUpdatedLabel(text);
    }

    protected String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }

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
