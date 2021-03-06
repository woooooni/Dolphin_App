package com.taewon.dolphin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<ContactItem> contactItemList;

    public ContactAdapter(Context context, List<ContactItem> contactItemList) {
        this.context = context;
        this.contactItemList = contactItemList;
    }


    //getters
    @Override
    public int getCount() {
        return contactItemList.size();
    }

    @Override
    public Object getItem(int i) { return contactItemList.get(i); }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.contact_item, null);
        TextView contactName = v.findViewById(R.id.contactName);
        TextView contactDept = v.findViewById(R.id.contactDept);
        contactName.setText(contactItemList.get(i).getContactUserName());
        contactDept.setText(contactItemList.get(i).getContactUserDept());

        ImageView contactCall = (ImageView)v.findViewById(R.id.contactCall);
        ImageView contactMessage = (ImageView)v.findViewById(R.id.contactMessage);
        contactCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.ic_baseline_call_24)
                        .setTitle("????????????")
                        .setMessage("????????? " + contactItemList.get(i).getContactUserName()+"????????? ????????? ????????????????")
                        .setNegativeButton("??????", null)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestCallMessage(contactItemList.get(i), "call");
                            }
                        })
                        .show();
            }
        });

        contactMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.ic_baseline_chat_24)
                        .setTitle("???????????????")
                        .setMessage("????????? " + contactItemList.get(i).getContactUserName()+"????????? ????????? ???????????????????")
                        .setNegativeButton("??????", null)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestCallMessage(contactItemList.get(i), "msg");
                            }
                        })
                        .show();
            }
        });
        return v;
    }

    private void requestCallMessage(ContactItem item, String type)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success)
                    {
                        switch (type)
                        {
                            case "msg":
                                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"+item.getContactUserPhoneNum()));
                                sendIntent.putExtra("sms_body", "???????????????."+UserData.getInstance().getUserName()+"?????????.");
                                context.startActivity(sendIntent);
                                break;
                            case "call":
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+item.getContactUserPhoneNum()));
                                context.startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "???????????? ??????????????????. ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT);
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(context, "???????????? ??????????????????. ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT);
                }

            }
        };
        RequestCallMessage validateRequest = new RequestCallMessage(UserData.getInstance().getUserName(), item.getContactUserName(), type, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(validateRequest);
    }

}
