package com.taewon.dolphin;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestFindId extends StringRequest {

    final private static String URL = "http://xodnjs2546.cafe24.com/findUserId.php";
    private Map<String, String> mHash;

    public RequestFindId(String userName, String userPhone, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        mHash = new HashMap<>();
        mHash.put("Token", "dolphin");
        mHash.put("userName", userName);
        mHash.put("userPhone", userPhone);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mHash;
    }
}
