package com.pixelro.graphqlsample;

import android.os.Bundle;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;
import com.apollographql.apollo.exception.ApolloException;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.com.pixelro.graphqlsample.AllMembersQuery;
import com.com.pixelro.graphqlsample.SignInMutation;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        setApollo();
        setJWT();
    }

    private void setJWT() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoiY2thM2g0ejhyMDAwMGo2NnczajVvdHMwMCIsImVtYWlsIjoiZXJAZW5raW5vLmNvbSIsIm5hbWUiOiLstZzsmIjsp4AiLCJ0ZWwiOiIwMTAyNDkwODk1NSJ9LCJpYXQiOjE1OTEwODUyMTAsImV4cCI6MTU5MTE3MTYxMH0.yZQgpDbelEwLj4sCuqC_zbf5_bzri9Ee3kcDqUZzWw4";

        try {
            JSONObject jsonObj = JWTUtils.getJson(token,"user");
            String email = (String) jsonObj.get("email");
            String name = (String) jsonObj.get("name");
            String id= (String) jsonObj.get("id");
            String tel = (String) jsonObj.get("tel");
            Log.i(">>>>>>>>>>>", email + " " + name + " " + id + " " + tel);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        JWT jwt = new JWT(token);
//        Date expiresAt = jwt.getExpiresAt();
//        boolean isExpired = jwt.isExpired(10); // 10 seconds leeway
//        String subject = jwt.getSubject();
//        Log.i(TAG, (expiresAt!=null)? "expiresAt : " + expiresAt.toString() : "expiresAt NULL");
//        Log.i(TAG, "isExpired : " + isExpired );
//        Log.i(TAG, (subject!=null)? "subject : " + subject.toString() : "subject NULL");
//
//
//        JSONObject user1 = Jwts.parser().parseClaimsJws(token).getBody().get("user",JSONObject.class);



//        Map<String, Claim> allClaims = jwt.getClaims();
//        Claim userClaim = jwt.getClaim("user");
//        String userStr = jwt.getClaim("user").asString();
//        JSONObject user1 = userClaim.  asObject(JSONObject.class);
//        JSONObject user2 = new JSONObject().;

//        Log.i(TAG, "userStr : " + userStr.toString());
//        Log.i(TAG, "user : " + user1.toString());
//
//        for (String item : list) {
//            Log.i(TAG, "user item : " + item );
//        }

//        JSONObject jsonUser2 = (JSONObject) allClaims.get("user").asObject(Class<JSONObject>);
//        JSONObject jsonUser2 = (JSONObject) allClaims.get("user").asObject(Class<JSONObject>);

//        try {

//            Log.i(TAG, "jsonUser : " + jsonUser2.toString() );
//            Log.i(TAG, "jsonUser2 : " + jsonUser2.get("id").toString() );
//            Log.i(TAG, "jsonUser2 : " + jsonUser2.get("email").toString() );
//            Log.i(TAG, "jsonUser2 : " + jsonUser2.get("name").toString() );
//            Log.i(TAG, "jsonUser2 : " + jsonUser2.get("tel").toString() );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
