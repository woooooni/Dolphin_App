package com.taewon.dolphin;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class GetFreeBoardRequest extends StringRequest {

    final private static String url = "https://xodnjs2546.cafe24.com/getFreeBoard.php";
    private Map<String, String> mHash;
    public GetFreeBoardRequest(Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mHash;
    }
}
