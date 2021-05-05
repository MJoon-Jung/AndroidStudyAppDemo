package com.example.studyapp.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.studyapp.HomeActivity;
import com.example.studyapp.R;
import com.example.studyapp.ui.chart.Env;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StopwatchActivity extends AppCompatActivity {

    TextView textView ;
    Button back_btn;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds, Hours ;
    String subject,today;

    boolean isFirst = false;

    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);textView = (TextView)findViewById(R.id.textView);

        //현재 날짜 불러오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        today = sdf.format(date);

        //과목정보 불러오기
        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");


        //Volley Queue  & request json
        requestQueue = Volley.newRequestQueue(getApplication());
        parseJson();

        back_btn = (Button)findViewById(R.id.back_btn);

        handler = new Handler() ;
        StartTime = SystemClock.uptimeMillis();


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StopwatchActivity.this, HomeActivity.class);
                startActivity(intent);
                if(isFirst){
                    InsertData();
                    isFirst = false;
                }else{
                    UpdateData();
                }
                handler.removeCallbacks(runnable);
            }
        });
    }

    private void InsertData(){
        String url = Env.SaveURL;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //json object >> {response:[{key : value}, {.....
                    JSONObject jsonObject = new JSONObject(response);
                    String res = jsonObject.getString("success");
                    Toast.makeText(StopwatchActivity.this, res, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })  {
            @Override
            protected Map<String, String> getParams() {
                String time = textView.getText().toString().replace(":","");
                Map<String,String> params = new HashMap<>();
                params.put("userID", Env.userID);
                params.put("study_date", today);
                params.put("study_subject", subject);
                params.put("study_time", time);
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }
    private void UpdateData(){
        String url = Env.ReSaveURL;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //json object >> {response:[{key : value}, {.....
                    JSONObject jsonObject = new JSONObject(response);
                    String res = jsonObject.getString("success");
                    Toast.makeText(StopwatchActivity.this, res, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })  {
            @Override
            protected Map<String, String> getParams() {
                String time = textView.getText().toString().replace(":","");
                System.out.println(Env.userID + " " + today + " " + subject + " " + time);
                Map<String,String> params = new HashMap<>();
                params.put("userID", Env.userID);
                params.put("study_date", today);
                params.put("study_subject", subject);
                params.put("study_time", time);
                System.out.println("업데이트 하러 왔습니다.");
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void parseJson(){
        String url = String.format(Env.fetchURL,Env.userID,today,subject);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            //json object >> {response:[{key : value}, {.....
                            JSONObject jsonObject = new JSONObject(response);

                            //object start name : response  >>>>> array
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject studyObject = jsonArray.getJSONObject(0);
                            String studyTime = studyObject.getString("study_time");

                            System.out.println("*******************");
                            System.out.println("study time :  " + studyTime);

                            if(!studyTime.equals("null")){
                                toTime(studyTime);
                            }else{
                                isFirst = true;
                            }
                            handler.postDelayed(runnable, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }
    private void toTime(String studyTime){
        String [] time = studyTime.split(":");
        Hours = Integer.parseInt(time[0]);
        Minutes = Integer.parseInt(time[1]);
        Seconds = Integer.parseInt(time[2]);
    }
    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            //시간의 흐름
            if((int)(UpdateTime / 1000) >= 1){
                Seconds += 1;

                Minutes += Seconds / 60;
                Seconds = Seconds % 60;
                Minutes = Minutes % 60;
                Hours += Minutes / 60;

                System.out.println("Hours : " + Hours + "  Minutes : " + Minutes + "  Seconds : " + Seconds);
            }
            System.out.println("러닝중");
            MilliSeconds = (int) (UpdateTime % 1000);


            //String format을 통한 시간 대입
            textView.setText(String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
            handler.postDelayed(this, 0);
        }
    };
}