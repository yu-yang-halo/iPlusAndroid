package cn.lztech.newiplus;

import android.app.Activity;
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

import cn.lztech.cache.HYLResourceUtils;


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
    }
    public Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Intent intent = new Intent(HYLLaunchActivity.this, HYLMainActivity.class);
                    HYLLaunchActivity.this.startActivity(intent);
                    HYLLaunchActivity.this.finish();
                    break;
                case 1:
                    Bitmap bm=null;
                    if(HYLResourceUtils.isUseCustomResource(HYLLaunchActivity.this)){
                        bm = BitmapFactory.decodeFile(HYLResourceUtils.userCustomUIResPath(HYLLaunchActivity.this)+"img/launchLogo.png");
                    }
                    if(bm==null){
                        logoImageView.setBackgroundResource(R.mipmap.launchlogo);
                    }else{
                        Drawable drawable =new BitmapDrawable(null,bm);
                        logoImageView.setBackgroundDrawable(drawable);
                    }
                    showOtherLogo(OTHERLOGO_DELAY_TIME);
                    break;
                default:
                    break;
            }
        }
    };

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
