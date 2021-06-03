package com.taewon.dolphin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class ActivityMyPage extends AppCompatActivity {
    /* Views */
    private CardView card_Logout;
    private CardView card_Secession;
    private CardView card_Notice;
    private CardView card_Freeboard;
    private TextView myPageUserDept;
    private TextView myPageUserName;
    private ImageView myPageBackBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        initViews();
        initListeners();
        initBehavior();
    }

    private void initViews()
    {
        /* Views */
        card_Logout = (CardView)findViewById(R.id.card_Logout);
        card_Secession = (CardView)findViewById(R.id.card_Secession);
        card_Notice = (CardView)findViewById(R.id.card_notice);
        myPageUserDept =(TextView)findViewById(R.id.myPageUserDept);
        myPageUserName =(TextView)findViewById(R.id.myPageUserName);
        card_Freeboard =(CardView)findViewById(R.id.card_Freeboard);
        myPageBackBtn = (ImageView)findViewById(R.id.MyPageBackBtn);
    }

    private void initListeners()
    {
        card_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref =getSharedPreferences("auto", ActivityLogin.MODE_PRIVATE);
                SharedPreferences.Editor editor =pref.edit();
                editor.clear();
                editor.commit();
                ActivityCompat.finishAffinity(ActivityMyPage.this);
                startActivity(new Intent(ActivityMyPage.this, ActivityStart.class));
                finish();
            }
        });

        card_Notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uc.ac.kr/www/index.php?pCode=ucnotice"));
                startActivity(intent);
            }
        });
        card_Freeboard.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMyPage.this, ActivityFreeBoard.class);
                startActivity(intent);
                finish();
            }
        }));

        card_Secession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMyPage.this);
                builder.setIcon(R.drawable.ic_baseline_block_24)
                        .setTitle("회원탈퇴")
                        .setMessage("정말로 탈퇴하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSecession();
                            }
                        }).setNegativeButton("취소",null).show();
            }
        });
        myPageBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initBehavior()
    {
        myPageUserName.setText(UserData.getInstance().getUserName());
        myPageUserDept.setText(UserData.getInstance().getUserDept());
    }

    private void requestSecession()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success)
                    {
                        SharedPreferences pref =getSharedPreferences("auto", ActivityLogin.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        Toast.makeText(ActivityMyPage.this, "탈퇴되었습니다.", Toast.LENGTH_LONG).show();
                        ActivityCompat.finishAffinity(ActivityMyPage.this);
                        startActivity(new Intent(ActivityMyPage.this, ActivityStart.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(ActivityMyPage.this, "오류로 인해 탈퇴하지 못했습니다..", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityMyPage.this, "오류로 인해 탈퇴하지 못했습니다..", Toast.LENGTH_LONG).show();
                }
            }
        };
        RequestUserSecession validateRequest = new RequestUserSecession(UserData.getInstance().getUserID(), UserData.getInstance().getUserStudentCode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityMyPage.this);
        queue.add(validateRequest);
    }
}