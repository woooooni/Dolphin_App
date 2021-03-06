package com.taewon.dolphin;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestUserStdCodeValidate extends StringRequest {

    private final static String URL = "http://xodnjs2546.cafe24.com/userStdCodeValidate.php";
    private Map<String, String> mHash;

    public RequestUserStdCodeValidate(String userStudentCode, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null); // 여기가 실질적으로 요청을 보내는 곳입니다.
        //요청에 의해 서버로 보낼 파라미터를 저장합니다.
        mHash.put("Token", "dolphin");
        mHash = new HashMap<>();
        mHash.put("userStudentCode", userStudentCode);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        //서버에서 가져갈 파라미터(HashMap<String, String> 형식)입니다.
        return mHash;
    }
}
