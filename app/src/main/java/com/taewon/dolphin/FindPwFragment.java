package com.taewon.dolphin;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class FindPwFragment extends Fragment {
    EditText findPwUserIdText;
    EditText findPwUserNameText;
    EditText findPwUserPhoneNumText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_find_pw, container, false);
        Button findPwBtn = root.findViewById(R.id.findPwBtn);
        findPwUserIdText = root.findViewById(R.id.findPwUserIdText);
        findPwUserNameText = root.findViewById(R.id.findPwUserNameText);
        findPwUserPhoneNumText = root.findViewById(R.id.findPwUserPhoneNumText);

        findPwBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String userID = findPwUserIdText.getText().toString();
                String userName = findPwUserNameText.getText().toString();
                String userPhoneNum = findPwUserPhoneNumText.getText().toString();

                if(userID.isEmpty() || userName.isEmpty() || userPhoneNum.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(R.drawable.ic_baseline_block_24)
                            .setTitle("?????? ????????????.")
                            .setMessage("\t??? ????????? ??????????????????.")
                            .setPositiveButton("??????",null)
                            .show();
                    return;
                }
                if(userPhoneNum.length()!=11 || !userPhoneNum.chars().allMatch(Character::isDigit))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(R.drawable.ic_baseline_block_24)
                            .setTitle("??????????????? ??????????")
                            .setMessage("\t??????????????? 11?????? ???????????????.")
                            .setPositiveButton("??????",null)
                            .show();
                    return;
                }
                requestFindPw(userID, userName, userPhoneNum);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    
    private void requestFindPw(String idText, String nameText, String phoneNumText)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success)
                    {
                        String userPassword = jsonObject.getString("userPassword");
                        TextView textview = new TextView(getContext());

                        String msg = "\t"+nameText+"?????? ??????????????? "+userPassword+"?????????.";
                        SpannableStringBuilder span = new SpannableStringBuilder(msg);
                        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.warning)),
                                msg.indexOf(" ???????????????")+7,
                                msg.indexOf(" ???????????????")+7+userPassword.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        textview.setText(span);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setIcon(R.drawable.icon_dolphins)
                                .setTitle("?????? ?????????.")
                                .setMessage(textview.getText())
                                .setPositiveButton("??????",null)
                                .show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setIcon(R.drawable.ic_baseline_block_24)
                                .setTitle("????????? ????????????.")
                                .setMessage("\t?????? ????????? ?????? ????????? ??????????????????.")
                                .setPositiveButton("??????",null)
                                .show();
                    }
                }
                catch (Exception e)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(R.drawable.ic_baseline_block_24)
                            .setTitle("????????? ????????????!")
                            .setMessage("\t????????? ??????????????? ?????????. \n\t?????? ?????? ?????? ??????????????????.")
                            .setPositiveButton("??????",null)
                            .show();
                }
            }
        };
        RequestFindPw request = new RequestFindPw(idText, nameText, phoneNumText, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }
}
