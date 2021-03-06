package com.taewon.dolphin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.jsoup.internal.StringUtil;

public class ActivityRegister extends AppCompatActivity implements View.OnClickListener{
//asd
    Boolean isValidateID = false;
    Boolean isValidateStudentCode = false;
    Boolean isCertified = false;
    Boolean isPassSame = false;
    private int userRandNum;

    /*Views*/
    private Button validateIDBtn;
    private Button validateStudentCodeBtn;
    private Button registerBtn;
    private Button certifyPhone;
    private Button certifyBtn;
    private EditText nameText;
    private EditText studentCodeText;
    private EditText idText;
    private EditText passwordText;
    private EditText passwordTextChk;
    private TextView warningText;
    private Spinner userMajor;
    private Spinner userDept;
    private EditText PHONE;
    private EditText certifyNum;
    private LinearLayout ceritifyZone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initListeners();

    }//onCreate_End

    //?????? ??????
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            //ID??????????????????
            case R.id.validateIDBtn:
                if(isPassIdRules())
                {
                    requestValidateID();
                }
                break;

            //???????????? ??????
            case R.id.validateStudentCodeBtn:
                //?????? ????????????
                if(isPassStdNumRules())
                {
                    requestValidateStdNum();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                    builder.setIcon(R.drawable.icon_dolphins)
                            .setTitle("????????? ??????")
                            .setMessage("\t?????? ???????????? ???????????????.\n\t?????? ???????????? ??????????????????.")
                            .setNegativeButton("??????", null)
                            .show();
                }
                break;

            //???????????? ?????? ??????
            case R.id.certifyPhone:
                //???????????? ??????.
                sendCertifyRandNum();
                break;

            //???????????? ?????? ??????
            case R.id.certifyBtn:
                chkCertifyRandNum();
                break;

            //???????????? ??????
            case R.id.registerBtn:
                if(isPassRegisterRules())
                {
                    requestRegister();
                }
                break;
        }
    }



    /* Methods */
    private void initViews(){
        isValidateID = false;
        isCertified = false;
        isPassSame = false;
        isValidateStudentCode = false;

        /* Views */
        validateIDBtn = (Button)findViewById(R.id.validateIDBtn);
        validateStudentCodeBtn=(Button)findViewById(R.id.validateStudentCodeBtn);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        certifyPhone = (Button)findViewById(R.id.certifyPhone);
        certifyBtn = (Button)findViewById(R.id.certifyBtn);

        nameText = (EditText)findViewById(R.id.nameText);
        studentCodeText = (EditText)findViewById(R.id.studentCodeText);
        idText = (EditText)findViewById(R.id.idText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        passwordTextChk = (EditText)findViewById(R.id.passwordTextChk);
        warningText = (TextView)findViewById(R.id.warningText);
        userMajor = (Spinner)findViewById(R.id.userMajor);
        userDept = (Spinner)findViewById(R.id.userDept);
        PHONE = (EditText)findViewById(R.id.PHONE);


        certifyNum = (EditText)findViewById(R.id.certifyNum);
        ceritifyZone = (LinearLayout)findViewById(R.id.certifyZone);
    }

    private void initListeners()
    {
        /* OnClickListeners */
        validateIDBtn.setOnClickListener(this);
        validateStudentCodeBtn.setOnClickListener(this);
        certifyPhone.setOnClickListener(this);
        certifyBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);


        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordText.getText().toString().equals(passwordTextChk.getText().toString()))
                {
                    warningText.setTextColor(Color.GREEN);
                    warningText.setText("??????????????? ???????????????.");
                    isPassSame = true;
                }
                else
                {
                    warningText.setTextColor(Color.RED);
                    warningText.setText("??????????????? ????????????.");
                    isPassSame = false;
                }
            }
        });

        //????????? ???????????? ????????? ????????? ???????????? ???????????????.
        passwordTextChk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                warningText.setVisibility(View.VISIBLE);
                if(passwordText.getText().toString().equals(passwordTextChk.getText().toString()))
                {
                    warningText.setTextColor(Color.GREEN);
                    warningText.setText("??????????????? ???????????????.");
                    isPassSame = true;
                }
                else
                {
                    warningText.setTextColor(Color.RED);
                    warningText.setText("??????????????? ????????????.");
                    isPassSame = false;
                }
            }


        });

        //?????????(Spinner)?????? ????????? ?????????,
        userMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = null;
                setDept(adapter, position);
            }
        });
    }


    //Id ????????? ????????? ???????????????.
    private boolean isPassIdRules()
    {
        String userID = idText.getText().toString();
        if(userID.equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setTitle("????????????????!").setMessage("\t???????????? ??????????????????.\n\t????????? ????????? ??????????????????.").setNegativeButton("??????", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        if(userID.length() < 4)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setTitle("???????????? ?????? ?????????.").setMessage("\t???????????? 4????????? ??????????????????.").setNegativeButton("??????", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }

    //????????? ????????? ?????? Id??? ??????????????? ???????????????.
    private void requestValidateID()
    {
        String userID = idText.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) { // ?????? ????????? ??????
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isExistUser = jsonObject.getBoolean("success");
                    if(isExistUser)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                        builder.setIcon(R.drawable.icon_dolphins).setTitle("????????? ?????????").setMessage("\t?????? ???????????? ??????????????????.\n\t?????? ???????????? ??????????????????.").setNegativeButton("??????", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                        builder.setIcon(R.drawable.icon_dolphins).setMessage("??? ???????????? ???????????????????").setNegativeButton("??????", null).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                idText.setBackgroundColor(getResources().getColor(R.color.LockColor));
                                idText.setEnabled(false);
                                validateIDBtn.setBackgroundColor(getResources().getColor(R.color.LockColor));
                                validateIDBtn.setEnabled(false);
                                isValidateID = true;
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("?????? ????????????.");
                    e.printStackTrace();
                }
            }
        };

        RequestUserIdValidate validateRequest = new RequestUserIdValidate(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityRegister.this);
        queue.add(validateRequest);
    }


    //????????? ????????? ????????? ???????????????.
    private boolean isPassStdNumRules()
    {
        String userStdCode = studentCodeText.getText().toString();
        if(!StringUtil.isNumeric(userStdCode) || !(userStdCode.length() == 7))
        {
            return false;
        }
        return true;
    }

    private void requestValidateStdNum()
    {
        String userStdCode = studentCodeText.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) { // ?????? ????????? ??????
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isExistUser = jsonObject.getBoolean("success");
                    if(isExistUser)
                    {
                        //????????? ???????????? ??????.
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                        builder.setIcon(R.drawable.icon_dolphins).setTitle("????????? ??????????????????.").setMessage("\t?????? ???????????? ???????????????.").setNegativeButton("??????", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                        builder.setIcon(R.drawable.icon_dolphins)
                                .setTitle("??? ?????? ????????? ??? ????????????.")
                                .setMessage("\t??? ???????????? ?????????????????")
                                .setNegativeButton("??????", null)
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        studentCodeText.setBackgroundColor(getResources().getColor(R.color.LockColor));
                                        studentCodeText.setEnabled(false);
                                        validateStudentCodeBtn.setBackgroundColor(getResources().getColor(R.color.LockColor));
                                        validateStudentCodeBtn.setEnabled(false);
                                        isValidateStudentCode = true;
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("?????? ????????????.");
                    e.printStackTrace();
                }
            }
        };
        RequestUserStdCodeValidate validateRequest2 = new RequestUserStdCodeValidate(userStdCode, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityRegister.this);
        queue.add(validateRequest2);
    }

    //????????? ????????? ????????? ????????????.
    private void sendCertifyRandNum()
    {
        String userPhone = PHONE.getText().toString();
        //?????? ????????? 11????????? ?????????, ????????? ???????????? ????????????.
        if(userPhone.length() != 11)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setTitle("???????????? ??????????").setMessage("\t?????? ?????? ???????????? 11?????? ????????????????").setNegativeButton("??????",null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        //1000~9999 ?????????????????? ????????????, ????????? ????????????.
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setTitle("????????? ??????").setMessage("\t??????????????? ?????? ??? ?????? ????????????!\n\t????????? ????????? ???????????????!");
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userRandNum = (int)((Math.random()*8999)+1000);
                    sendSMS(userPhone, "Dolphin ????????? ?????? ????????????: "+Integer.toString(userRandNum));
                    ceritifyZone.setVisibility(View.VISIBLE);
                }
            });
            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();

        }
        catch (Exception e)
        {
            //????????? ???????????? ?????????, ???????????? ????????? ???????????? ??????????????????, ???????????? ????????? ????????? ????????? ??? ????????? ?????????.
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins)
                    .setTitle("??? ??????")
                    .setMessage("\t?????? ?????? ???????????? ???????????????, ?????????????????? ??????>?????? ?????? ?????? ????????? ??????????????????.")
                    .setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+getApplicationContext().getPackageName()));
                            startActivity(intent);
                            dialog.cancel();;
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        //???????????? ?????? ????????? ?????? ????????? ??? ??????, ?????? ??????????????? ???????????????.
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .show();
        }
    }

    //???????????? ????????? ???????????????.
    private void setDept(ArrayAdapter<String> adapter, int position)
    {
        switch (position)
        {
            case 0:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.it));
                userDept.setAdapter(adapter);
                break;
            case 1:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.nursing));
                userDept.setAdapter(adapter);
                break;
            case 2:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.early_childhood_education));
                userDept.setAdapter(adapter);
                break;
            case 3:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.sports_guidance));
                userDept.setAdapter(adapter);
                break;
            case 4:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.architectural_design));
                userDept.setAdapter(adapter);
                break;
            case 5:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.physiotherapy));
                userDept.setAdapter(adapter);
                break;
            case 6:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.socialwelfare));
                userDept.setAdapter(adapter);
                break;
            case 7:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.mechanical_Engineering));
                userDept.setAdapter(adapter);
                break;
            case 8:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.dental_hygiene));
                userDept.setAdapter(adapter);
                break;
            case 9:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.tax_accounting));
                userDept.setAdapter(adapter);
                break;
            case 10:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.electrical_and_electronic_engineering));
                userDept.setAdapter(adapter);
                break;
            case 11:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.food_and_nutrition));
                userDept.setAdapter(adapter);
                break;
            case 12:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.distribution_and_logistics_management));
                userDept.setAdapter(adapter);
                break;
            case 13:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.safety_and_industrial_engineering));
                userDept.setAdapter(adapter);
                break;
            case 14:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.hotel_culinary_and_bakery));
                userDept.setAdapter(adapter);
                break;
            case 15:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.global_business));
                userDept.setAdapter(adapter);
                break;
            case 16:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.digital_content_design));
                userDept.setAdapter(adapter);
                break;
            case 17:
                adapter = new ArrayAdapter<>(ActivityRegister.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.chemical_engineering));
                userDept.setAdapter(adapter);
                break;
            default:
                break;
        }

    }

    private void chkCertifyRandNum()
    {
        int userInput;
        try {
            userInput = Integer.parseInt(certifyNum.getText().toString());
            //???????????? ?????? ?????????, ???????????? ????????? ????????? ????????? ???????????????.
            if(isCompareCertifyNum(userInput, userRandNum))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                builder.setMessage("\t?????????????????????.")
                        .setNegativeButton("??????",null)
                        .show();
                isCertified = true;
                ceritifyZone.setVisibility(View.GONE);
                certifyPhone.setEnabled(false);
                certifyPhone.setBackgroundColor(getResources().getColor(R.color.LockColor));
                PHONE.setEnabled(false);
                PHONE.setBackgroundColor(getResources().getColor(R.color.LockColor));
                certifyBtn.setEnabled(false);
                certifyBtn.setBackgroundColor(getResources().getColor(R.color.LockColor));
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                builder.setIcon(R.drawable.icon_dolphins)
                        .setTitle("?????? ????????? ?????????.")
                        .setMessage("\t????????? ???????????? ??????????")
                        .setNegativeButton("??????",null)
                        .show();
            }
        }
        catch (Exception e)
        {
            //???????????? ????????????, Integer.parseInt?????? ????????? ??????????????????, ?????? ????????? ??????????????????.
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins)
                    .setTitle("????????? 4?????? ????????? ????????????")
                    .setMessage("\t4?????? ????????? ????????? ??????????????????!")
                    .setNegativeButton("??????",null)
                    .show();
        }
    }

    //???????????? ????????? ??????????????? ???????????????.
    private boolean isPassRegisterRules()
    {
        String uStudentCode = studentCodeText.getText().toString();
        String uID = idText.getText().toString();
        String uPassword = passwordText.getText().toString();
        String uName = nameText.getText().toString();
        String uMajor = userMajor.getSelectedItem().toString();
        String uDept = userDept.getSelectedItem().toString();
        String uPhone = PHONE.getText().toString();
        //ID??? ?????? ???????????? ????????? ???.
        if(!isValidateID || !isValidateStudentCode)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setMessage("\t?????? ????????? ????????????.").setNegativeButton("??????",null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        //???????????? ?????? ???.
        else if(!isPassSame)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setTitle("??????????????? ????????????.").setMessage("\t?????? ??????????????? ??????????????????.").setNegativeButton("??????",null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        //????????? ?????? ????????? ???.
        else if(!isCertified)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins).setMessage("\t???????????? ??????????????????.").setNegativeButton("??????",null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        if(uID.isEmpty() || uPassword.isEmpty() || uName.isEmpty() || uMajor.isEmpty() || uDept.isEmpty() || uPhone.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.ic_baseline_block_24)
                    .setTitle("??? ????????? ????????????.")
                    .setMessage("\t????????? ??????????????? ??????????????????!")
                    .setNegativeButton("??????",null)
                    .show();
            return false;
        }
        else if(uPassword.length()<6)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
            builder.setIcon(R.drawable.icon_dolphins)
                    .setTitle("??????????????? ?????? ????????????.")
                    .setMessage("\t??????????????? 6?????? ?????????????????? ????????????.")
                    .setNegativeButton("??????",null)
                    .show();
            return false;
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDeviceInfo(String userID){
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        RequestUpdateDeviceInfo requestDolphinNoticeChk =
                new RequestUpdateDeviceInfo(userID, tm.getImei(), tm.getDeviceId());
        RequestQueue queue = Volley.newRequestQueue(ActivityRegister.this);
        queue.add(requestDolphinNoticeChk);
    }

    //??????????????? ???????????????.
    private void requestRegister()
    {
        String uName = nameText.getText().toString();
        String uStudentCode = studentCodeText.getText().toString();
        String uID = idText.getText().toString();
        String uPassword = passwordText.getText().toString();
        String uMajor = userMajor.getSelectedItem().toString();
        String uDept = userDept.getSelectedItem().toString();
        String uPhone = PHONE.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) { // ?????? ????????? ??????
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                        builder.setTitle("?????? ????????????.")
                                .setMessage("\t???????????? ????????? ?????????????")
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
                                        intent.putExtra("userID", uID);
                                        intent.putExtra("userPW", uPassword);
                                        updateDeviceInfo(uID);
                                        startActivity(intent);
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                        builder.setIcon(R.drawable.ic_baseline_block_24)
                                .setTitle("??? ????????????..?")
                                .setMessage("\t?????? ??? ???????????????.\n\t??????????????? ???????????????\n\t010-4640-7993")
                                .setNegativeButton("??????", null)
                                .show();
                    }
                }
                catch (Exception e)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                    builder.setIcon(R.drawable.ic_baseline_block_24)
                            .setTitle("???????????????!")
                            .setMessage("\t????????? ??????????????? ?????????.\n\t????????? ??????????????? ?????? ??????????????????.")
                            .setNegativeButton("??????", null)
                            .show();
                    e.printStackTrace();
                }
            }
        };
        RequestUserRegister validateRequest = new RequestUserRegister(uStudentCode, uID, uPassword, uName, uMajor, uDept, uPhone, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityRegister.this);
        queue.add(validateRequest);
    }

    //???????????? ????????????.
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT),0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED),0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK :
                        Toast.makeText(getBaseContext(),"?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    //??? ????????? ????????? ???????????????.
    private Boolean isCompareCertifyNum(int num1, int num2){
        return num1 == num2;
    }

}
