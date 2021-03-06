package cn.lztech.newiplus;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.elnet.andrmb.elconnector.WSConnector;
import cn.lztech.WifiAdmin;
import cn.lztech.cache.HYLResourceUtils;
import cn.lztech.cache.HYLSharePreferences;
import cn.lztech.cache.HYLUserResourceConfig;
import cn.lztech.curl.CurlUtils;


/**
 * Created by Administrator on 2015/7/23.
 */
public class HYLLaunchActivity extends Activity {
    private static  final  int HYLLOGO_DELAY_TIME=2;
    private static  final  int OTHERLOGO_DELAY_TIME=3;
    ImageView logoImageView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        logoImageView= (ImageView) findViewById(R.id.imageView);
        Animation animation=AnimationUtils.loadAnimation(HYLLaunchActivity.this, android.R.anim.fade_in);
        logoImageView.setAnimation(animation);

        showHYLLogo(HYLLOGO_DELAY_TIME);
        String serverIP= HYLSharePreferences.getServerIP(this);

        if(serverIP!=null){
            WSConnector.getInstance(serverIP,"8080",false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        HYLResourceUtils.loadAppTagJSON(this);
    }

    public Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    HYLLaunchActivity.this.toMainPage();
                    break;
                case 1:
                    if(HYLResourceUtils.isUseCustomResource(HYLLaunchActivity.this)){
                        Bitmap bm= BitmapFactory.decodeFile(HYLResourceUtils.userCustomUIResPath(HYLLaunchActivity.this)+"img/launchLogo.png");
                        if(bm==null){
                            logoImageView.setBackgroundResource(R.mipmap.logo);
                        }else{
                            Drawable drawable =new BitmapDrawable(null,bm);
                            logoImageView.setBackgroundDrawable(drawable);
                        }
                        showOtherLogo(OTHERLOGO_DELAY_TIME);
                    }else{
                        HYLLaunchActivity.this.toMainPage();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void toMainPage(){
        Intent intent = new Intent(this, HYLMainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private  void  showOtherLogo(int delay){
        ScheduledExecutorService scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                mhandler.sendMessage(msg);
            }
        }, delay, TimeUnit.SECONDS);
    }

    private  void  showHYLLogo(int delay){
        ScheduledExecutorService scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                msg.what=1;
                mhandler.sendMessage(msg);
            }
        },delay,TimeUnit.SECONDS);
    }



}
