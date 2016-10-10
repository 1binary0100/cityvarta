package com.newsvarta.dvimaniya;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsvarta.dvimaniya.adapter.NewsRecycler;
import com.newsvarta.dvimaniya.lists.NewsList;
import com.newsvarta.dvimaniya.utils.AppUtil;
import com.newsvarta.dvimaniya.utils.DataBaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CustomInterfaces.CustomInterface;

public class MainActivity extends FragmentActivity implements CustomInterface{

    public static final String TAG = "MainActivity";
    RequestQueue queue;
    List<NewsList> newsList;
    CustomInterface customInterface;
    private RecyclerView mRecyclerView;
    private NewsRecycler newsAdapter;
    AppUtil appUtil;
    String NewsURL, PostInstallURL, deviceId, ip, mac, name, androidOS;
    DataBaseHandler dataBaseHandler;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appUtil = new AppUtil(getApplicationContext());
        NewsURL = getResources().getString(R.string.APIURL)+"v1/news";
        PostInstallURL = getResources().getString(R.string.APIURL)+"v1/postinstall";
        context = MainActivity.this;
        dataBaseHandler = new DataBaseHandler(this);
        //startService()

        if(appUtil.isNetworkAvailable()) { //check if internet is there
            getNews(); //get news
            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun) //Call first time install method
            {
                postInstall();
                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.apply();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        customInterface = new CustomInterface() {
            @Override
            public void openFrag(String url) {
                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class).putExtra("url", url);
                startActivity(intent);

            }
        };

    }

    public void getNews(){

            queue = Volley.newRequestQueue(getApplicationContext()); //initiate the instance
           /* JsonObjectRequest getNews = new JsonObjectRequest(Request.Method.GET, NewsURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Toast.makeText(getApplicationContext(), response + "", Toast.LENGTH_SHORT).show();
                            //tvHello.setText(response.toString());
                            parseResult(response.toString());
                            newsAdapter = new NewsRecycler(MainActivity.this, newsList,customInterface );
                            mRecyclerView.setAdapter(newsAdapter);
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(getApplicationContext(), "error " + "", Toast.LENGTH_SHORT).show();
                            //tvHello.setText("error");
                        }
                    }
            );*/
        Log.e(TAG,"request started");
        StringRequest getNews = new StringRequest(Request.Method.POST, NewsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //VolleyLog.d(TAG, "Error: " + response.toString());
                //Log.e(TAG +"Response", response);
                parseResult(response.toString());
                newsAdapter = new NewsRecycler(MainActivity.this, dataBaseHandler.getPosts(),customInterface );
                mRecyclerView.setAdapter(newsAdapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.e(TAG + "Error", error + "as");

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lastUpdatedEpoch", dataBaseHandler.getLastSync());
                Log.e(TAG + "Params", params.toString() +"fh");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        Log.e(TAG +"request", getNews.toString());
        queue.add(getNews); //add request in the queue

    }

    private void parseResult(String s) {
        try {
            JSONObject response = new JSONObject(s);
            JSONArray news = response.getJSONArray("news");
            newsList = new ArrayList<>();
            Log.e(TAG + "Response", s + "as");
            for (int i=0; i <news.length(); i++){
                JSONObject newsp = news.getJSONObject(i);
                NewsList item = new NewsList();
                item.setTitle(newsp.getString("postTitle"));
                item.setNews(newsp.getString("postText"));
                item.setThumbnail(newsp.getString("imgUrl"));
                item.setSourceUrl(newsp.getString("sourceLink"));
                item.setSource(newsp.getString("sourceName"));
                item.setTimeStamp(newsp.getString("timeStamp"));
                item.setUpdated(newsp.getString("updatedEpoch"));
                dataBaseHandler.addPosts(item);
                //newsList.add(item);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    public void postInstall(){
        deviceId   = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID); //Get unique device id
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
         ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()); //Get install time ip
         mac = info.getMacAddress(); //get mac
         name = "";
         androidOS = Build.VERSION.RELEASE;
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
       // Account[] list = manager.getAccounts();
        /*for(Account account:list){
            if(account.type.equalsIgnoreCase("com.google")){
                name = account.name;

            }
        }*/

        StringRequest postInstall = new StringRequest(Request.Method.POST, PostInstallURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Error: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device", "android");
                params.put("deviceId", deviceId);
                params.put("ipAddress", ip);
                params.put("macAddress", mac);
                params.put("installName", name);
                params.put("osVersion", androidOS);
                params.put("installEmail", "1");
                Log.d(TAG, params.toString());
                return params;
            }
        };
        Log.e(TAG, postInstall.toString());
        queue.add(postInstall);

    }



    @Override
    public void openFrag(String url) {

    }
}
