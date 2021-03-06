package com.taewon.dolphin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityFreeBoardViewer extends AppCompatActivity {
    public static Context mContext;

    Intent freeBoardIntent;

    private ListView freeBoardCommentListView;
    private EditText writeCommentEditText;
    private ImageButton writeCommentBtn;
    private String BoardID;
    private LinearLayout commentArea;
    private TextView writer;
    private TextView writtenDate;
    private TextView board_title;
    private TextView board_contents;
    private LinearLayout profileLayout;
    private LinearLayout udBtns;
    private ImageButton FreeBoardViewBackBtn;
    private TextView deleteBtn;
    private TextView modifyBtn;
    private ImageView writer_img;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_viewer);
        mContext = this;

        initViews();
        initListeners();
        initBehaviors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLoadComments();
    }


    /* Custom Methods */
    private void initViews()
    {
        //Name, Date, Title, Contents, userPhone, BoardID ?????? ?????? ??????????????????.
        freeBoardIntent = getIntent();
        BoardID = freeBoardIntent.getStringExtra("BoardID");
        //????????? ????????????.
        writer = (TextView) findViewById(R.id.writer);
        writtenDate = (TextView) findViewById(R.id.writtenDate);
        board_title = (TextView) findViewById(R.id.board_title);
        board_contents = (TextView) findViewById(R.id.board_contents);
        profileLayout = (LinearLayout) findViewById(R.id.board_profileLayout);

        //?????? ???????????????.
        commentArea = (LinearLayout)findViewById(R.id.commentArea);
        freeBoardCommentListView = (ListView) findViewById(R.id.freeBoardCommentListView);
        writeCommentEditText = (EditText) findViewById(R.id.writeCommentEditText);
        writeCommentBtn = (ImageButton) findViewById(R.id.writeCommentBtn);

        udBtns = (LinearLayout) findViewById(R.id.udBtns);
        FreeBoardViewBackBtn = (ImageButton) findViewById(R.id.FreeBoardViewBackBtn);
        deleteBtn = (TextView) findViewById(R.id.deleteBtn);
        modifyBtn = (TextView) findViewById(R.id.modifyBtn);
        writer_img = (ImageView) findViewById(R.id.writer_img);

    }

    private void initListeners()
    {
        //onClickListener???
        //???????????? ???????????????.
        FreeBoardViewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //profile??? ????????? ????????? ??? ??? ????????? ?????????.
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(freeBoardIntent.getStringExtra("userPhone").equals(UserData.getInstance().getUserPhoneNum()))
                {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFreeBoardViewer.this);
                builder.setIcon(R.drawable.icon_dolphins)
                        .setTitle("????????????")
                        .setMessage("????????? " + freeBoardIntent.getStringExtra("Name") + "????????? ????????? ???????????????????")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestCallMessage(ActivityFreeBoardViewer.this);
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
            }
        });

        //?????? ?????? ?????????,
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFreeBoardViewer.this);
                builder.setIcon(R.drawable.ic_baseline_block_24)
                        .setTitle("????????? ??????")
                        .setMessage("????????? ?????????????????????????")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestDeleteBoard();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
            }
        });


        //?????? ???????????? ?????????,
        writeCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????? ????????????.
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(ActivityFreeBoardViewer.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(writeCommentEditText.getWindowToken(), 0);

                // ??????????????? ?????? ?????????, ????????? ????????????????????????.
                writeCommentBtn.setBackgroundColor(getResources().getColor(R.color.LockColor));
                writeCommentBtn.setEnabled(false);
                requestWriteComment();
            }
        });
    }

    private void initBehaviors()
    {
        //1. ???????????? Intent?????? TextView ????????? ?????????
        writer.setText(freeBoardIntent.getStringExtra("Name"));
        writtenDate.setText(freeBoardIntent.getStringExtra("Date"));
        board_title.setText(freeBoardIntent.getStringExtra("Title"));
        board_contents.setText(freeBoardIntent.getStringExtra("Contents"));
        //2. ?????? ?????? ????????? ????????????, ?????? ????????? ???????????? ?????????.
        if (freeBoardIntent.getStringExtra("userID").equals(UserData.getInstance().getUserID())) {
            udBtns.setVisibility(View.VISIBLE);
            writer_img.setImageResource(R.drawable.icon_dolphins);
        }

    }

    private void requestCallMessage(Context context)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success)
                    {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + freeBoardIntent.getStringExtra("userPhone")));
                        startActivity(intent);
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
        RequestCallMessage validateRequest = new RequestCallMessage(UserData.getInstance().getUserName(), freeBoardIntent.getStringExtra("Name"), "call", responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(validateRequest);
    }

    //???????????? ??????????????? ???????????? ???????????????.
    public void requestLoadComments(){
        //????????? ???????????????.
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("DATA");
                    List<CommentItem> commentItemList = new ArrayList<>();
                    String userName, userID, date, userComment;
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        userName = obj.get("userName").toString();
                        userID = obj.get("userID").toString();
                        userComment = obj.get("userComment").toString();
                        date = obj.get("date").toString();
                        CommentItem instance = new CommentItem(freeBoardIntent.getStringExtra("BoardID"), userName, userID, date, userComment);
                        commentItemList.add(instance);
                    }
                    if(commentItemList.size()==0)
                    {
                        commentArea.setVisibility(View.GONE);
                        return;
                    }
                    else
                    {
                        commentArea.setVisibility(View.VISIBLE);
                        CommentAdapter adapter = new CommentAdapter(ActivityFreeBoardViewer.this, commentItemList);
                        freeBoardCommentListView.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(freeBoardCommentListView);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
        RequestGetComment validateRequest = new RequestGetComment(freeBoardIntent.getStringExtra("BoardID"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityFreeBoardViewer.this);
        queue.add(validateRequest);
    }


    //????????? ????????? ????????? ???????????? ???????????????.
    private void requestWriteComment()
    {
        //????????? ????????? ????????? ???????????? ???????????????.
        String comment = writeCommentEditText.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success)
                    {
                        //?????? ?????????, ?????????????????? ?????? ???????????????.
                        requestLoadComments();
                        writeCommentBtn.setEnabled(true);
                        writeCommentBtn.setBackgroundColor(getResources().getColor(R.color.Dolphin));

                        writeCommentEditText.setText("");
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFreeBoardViewer.this);
                        builder.setIcon(R.drawable.icon_dolphins)
                                .setTitle("??????")
                                .setMessage("????????? ???????????? ???????????????. ?????? ??? ?????? ????????? ?????????!")
                                .setPositiveButton("??????",null)
                                .show();
                    }
                }
                catch (Exception e)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFreeBoardViewer.this);
                    builder.setIcon(R.drawable.icon_dolphins)
                            .setTitle("??????")
                            .setMessage("????????? ???????????? ???????????????. ?????? ??? ?????? ????????? ?????????!")
                            .setPositiveButton("??????",null)
                            .show();
                    e.printStackTrace();
                }
            }
        };
        RequestCommentWrite validateRequest = new RequestCommentWrite(BoardID, UserData.getInstance().getUserName(), UserData.getInstance().getUserID(), comment, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityFreeBoardViewer.this);
        queue.add(validateRequest);
    }

    //????????? ?????? ????????? ???????????????.
    private void requestDeleteBoard()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success)
                    {
                        Toast.makeText(ActivityFreeBoardViewer.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(ActivityFreeBoardViewer.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityFreeBoardViewer.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        RequestFreeBoardDelete validateRequest = new RequestFreeBoardDelete(freeBoardIntent.getStringExtra("BoardID"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityFreeBoardViewer.this);
        queue.add(validateRequest);
    }

    //??????????????? ?????? ????????? ????????? ???????????? ???????????? ???????????????.
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        if(listAdapter.getCount()> 0)
        {
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight = listItem.getMeasuredHeight()* listAdapter.getCount();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params); listView.requestLayout();
    }
    
}
