package com.taewon.dolphin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

public class ActivityStart extends AppCompatActivity {

    private static final int MULTIPLE_PERMISSION = 10235;
    private LinearLayout startLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startLogo = (LinearLayout)findViewById(R.id.startLogo);
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.wrap_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityStart.this, ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        chkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void launchNext()
    {
        //1.
        Animation boom_up_anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);
        //2.
        boom_up_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    //3??? ?????????
                    @Override
                    public void run() {
                        Intent intent = new Intent(ActivityStart.this, ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        });
        //3.
        startLogo.startAnimation(boom_up_anim);
    }

    private void chkPermissions()
    {
        /* ????????? ????????? ???????????????. */
        String[] PEMISSIONS = { Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE };
        if(hasPermissions(this, PEMISSIONS)){
            ActivityCompat.requestPermissions(this, PEMISSIONS, MULTIPLE_PERMISSION);
        }
        else
        {
            launchNext();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //????????? ?????? ?????? ????????? ???????????? ????????????.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case  MULTIPLE_PERMISSION:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    launchNext();
                }
                else{
                    //???????????? ???????????? ????????? ????????????, ???????????? ????????? ????????? ????????? ??? ????????? ?????????.
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setIcon(R.drawable.icon_dolphins).setTitle("??? ??????").setMessage("?????? ?????? ???????????? ???????????????, ?????????????????? ??????>?????? ?????? ?????? ????????? ??????????????????.");
                    builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                        //?????? ????????? ?????????, ???????????? ????????? ????????? ???????????????.
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+getApplicationContext().getPackageName()));
                            startActivity(intent);
                            dialog.cancel();;
                        }
                    });
                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        //??????????????? ?????????, ?????? ???????????????.
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            System.exit(0);
                        }
                    });
                    builder.show();
                }
        }
    }

    public static boolean hasPermissions(Context context, String[] permissions)
    {
        // ???????????? ????????? ????????? ??????????????? ???????????????.
        if(context != null && permissions != null)
        {
            for(String permission : permissions)
            {
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_DENIED)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
