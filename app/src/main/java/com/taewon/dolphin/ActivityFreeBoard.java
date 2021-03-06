package com.taewon.dolphin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityFreeBoard extends AppCompatActivity{ //클릭 리스너 인터페이스 상속
    private FreeBoardAdapter freeBoardAdapter;
    private ListView freeBoardListView;
    private List<FreeBoardItem> freeBoardItemList;
    private FloatingActionButton writingBtn;
    private ImageButton freeBoardBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard);
        initViews();
        initListeners();
    }

    @Override
    protected void onResume() {
        //사용자가 FreeBoardActivity로 돌아오면 모든 자유게시판을 로드합니다.
        super.onResume();
        requestLoadAllFreeBoard();
    }


    /* Custom Methods */
    private void initViews()
    {
        freeBoardListView = (ListView)findViewById(R.id.freeBoardListView);
        freeBoardItemList = new ArrayList<>();
        writingBtn = (FloatingActionButton)findViewById(R.id.writingBtn);
        freeBoardBackBtn = (ImageButton)findViewById(R.id.FreeBoardBackBtn);
    }

    private void initListeners()
    {
        //자유게시판의 글을 클릭하면 실행.
        freeBoardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //실행할거 적으세요..
                Intent intent = new Intent(ActivityFreeBoard.this, ActivityFreeBoardViewer.class);
                //누른 게시판의 인스턴스를 생성해 FreeBoardViewerActivity.class 인텐트로 넘겨준다.
                FreeBoardItem instance = freeBoardItemList.get(position);
                intent.putExtra("Name", instance.getUserName());
                intent.putExtra("userID", instance.getUserID());
                intent.putExtra("Date", instance.getDate());
                intent.putExtra("Title", instance.getTitle());
                intent.putExtra("Contents", instance.getContents());
                intent.putExtra("userPhone", instance.getUserPhone());
                intent.putExtra("BoardID", Integer.toString(instance.getBoardId()));
                startActivity(intent);
            }
        });

        //글쓰기버튼 누르면, 실행
        writingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFreeBoard.this, ActivityFreeBoardWriting.class);
                startActivity(intent);
            }
        });

        //뒤로가기 버튼 누르면, 실행
        freeBoardBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestLoadAllFreeBoard()
    {
        freeBoardItemList.clear();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("DATA");
                    int boardID;

                    for(int i=0; i<jsonArray.length(); i++)
                    {
                        String title, contents, date, userName, userID, PHONE, commentCount;
                        JSONObject object = jsonArray.getJSONObject(i);
                        title = object.get("title").toString();
                        contents = object.get("contents").toString();
                        date = ActivityMain.calDate_ShouldReturnString(object.get("DATE").toString());
                        userName = object.get("userName").toString();
                        userID = object.get("userID").toString();
                        PHONE = object.get("PHONE").toString();
                        boardID = object.getInt("no");
                        commentCount = object.get("commentCount").toString();
                        freeBoardItemList.add(new FreeBoardItem(boardID, title, contents, userName, userID, PHONE, date, commentCount));
                    }
                    freeBoardAdapter = new FreeBoardAdapter(ActivityFreeBoard.this, freeBoardItemList);
                    freeBoardListView.setAdapter(freeBoardAdapter);
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFreeBoard.this);
                    builder.setIcon(R.drawable.icon_dolphins)
                            .setTitle("오류")
                            .setMessage("게시판을 불러오는 중 오류가 발생했습니다.")
                            .setPositiveButton("확인",null)
                            .show();
                    e.printStackTrace();
                }
            }
        };
        RequestGetFreeBoard validateRequest = new RequestGetFreeBoard(responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityFreeBoard.this);
        queue.add(validateRequest);
    }
}