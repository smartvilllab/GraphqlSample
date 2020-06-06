package com.pixelro.graphqlsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.auth0.android.jwt.JWT;
import com.com.pixelro.graphqlsample.AllMembersQuery;
import com.com.pixelro.graphqlsample.SignInMutation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FirstFragment extends Fragment {
    public static final String PREFERENCES_NAME = "my_preference";
    private Context mContext;
    private final String TAG = this.getClass().toString();
    private ApolloClient apolloClient;
    private View layout;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }
    
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key,"");
        return value;
    }


    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = this.getContext();
        layout = view;

        showToken(layout);

        view.findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                // 토큰 삭제
                ((TextView)view.findViewById(R.id.textview_token)).setText("");
                ((TextView)view.findViewById(R.id.textview_decode)).setText("");
                setString(mContext,"token","");
            }
        });

        view.findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인
                setApollo();
                signIn("er@enkino.com","1234");
            }
        });

    }

    private void showToken(View view) {
// 로칼에 저장된 토큰 표시
        String token = getString(mContext, "token");
        ((TextView)view.findViewById(R.id.textview_token)).setText(token);

        // 토큰이 있는 경우 디코드 하기
        if (token!=null && !"".equals(token)) {
            try {
                String decodeStr = null;
                JWT jwt = new JWT(token);
                boolean isExpired = jwt.isExpired(10); // 10 초 전까지 토큰 종료 여부 판단
                decodeStr = JWTUtils.decoded(token); // 디코드 값
                try {
                    JSONObject jsonObj = JWTUtils.getJson(token,"user");
                    String email = (String) jsonObj.get("email");
                    String name = (String) jsonObj.get("name");
                    String id= (String) jsonObj.get("id");
                    String tel = (String) jsonObj.get("tel");
                    Log.i(">>>>>>>>>>>", email + " " + name + " " + id + " " + tel);

                    decodeStr += "\nJSON :\n" + email + "\n" + id + "\n" + name + "\n" + tel;
                    ((TextView)view.findViewById(R.id.textview_decode)).setText(decodeStr );

                } catch (Exception e) {
                    e.printStackTrace();
                    ((TextView)view.findViewById(R.id.textview_decode)).setText("decode error : " + e.getLocalizedMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                ((TextView)view.findViewById(R.id.textview_decode)).setText("decode error : " + e.getLocalizedMessage());
            }
        }
    }

    private void setApollo() {

        ApolloSqlHelper apolloSqlHelper = ApolloSqlHelper.create(this.getContext(), "db_eyelab");
        NormalizedCacheFactory cacheFactory = new SqlNormalizedCacheFactory(apolloSqlHelper);

        // Create the cache key resolver, this example works well when all types have globally unique ids.
        CacheKeyResolver resolver = new CacheKeyResolver() {
            @NotNull
            @Override
            public CacheKey fromFieldRecordSet(@NotNull ResponseField field, @NotNull Map<String, Object> recordSet) {
                return formatCacheKey((String) recordSet.get("id"));
            }

            @NotNull
            @Override
            public CacheKey fromFieldArguments(@NotNull ResponseField field, @NotNull Operation.Variables variables) {
                return formatCacheKey((String) field.resolveArgument("id", variables));
            }

            private CacheKey formatCacheKey(String id) {
                if (id == null || id.isEmpty()) {
                    return CacheKey.NO_KEY;
                } else {
                    return CacheKey.from(id);
                }
            }
        };

        //Build the Apollo Client , http client
//        String authHeader = "Bearer $accessTokenId";
//        final String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoiY2thM2g0ejhyMDAwMGo2NnczajVvdHMwMCIsImVtYWlsIjoiZXJAZW5raW5vLmNvbSIsIm5hbWUiOiLstZzsmIjsp4AiLCJ0ZWwiOiIwMTAyNDkwODk1NSJ9LCJpYXQiOjE1OTEwODUyMTAsImV4cCI6MTU5MTE3MTYxMH0.yZQgpDbelEwLj4sCuqC_zbf5_bzri9Ee3kcDqUZzWw4";
//        final String authHeader = "";

        String authHeader = "";
        String token = getString(mContext, "token");
        if (token != null && !"".equals(token)) {
            authHeader = "Bearer " + token;
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        final String finalAuthHeader = authHeader;
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", finalAuthHeader)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient okHttpClient = httpClient.build();

        apolloClient = ApolloClient.builder()
                .serverUrl("https://pixelro-api.an.r.appspot.com/graphql")
                .normalizedCache(cacheFactory, resolver)
                .okHttpClient(okHttpClient)
                .build();

    }

    private void signIn(String email, String password) {
//        SignInMutation signInMutation = SignInMutation.builder().email("er@enkino.com").password("1234").build();

        SignInMutation signInMutation = SignInMutation.builder().email(email).password(password).build();
        AllMembersQuery allMembersQuery = AllMembersQuery.builder().build();


        Handler uiHandler = new Handler(Looper.getMainLooper()) {  // 핸들러에 Main Looper를 인자로 전달
            @Override
            public void handleMessage(Message msg) {  // 메인 스레드에서 호출
                Log.d(TAG,"handleMessage : " + msg.what);
                //화면 수정
                showToken(layout);
            }
        };

        //
        apolloClient.mutate(signInMutation).enqueue(
                new ApolloCallback<SignInMutation.Data>(new ApolloCall.Callback<SignInMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SignInMutation.Data> response) {
                        Log.i(TAG, response.toString());
                        String error = response.data().signIn().error();
                        if (error==null) {
                            String token = response.data().signIn().token();
                            if (token==null)
                                Log.i(TAG, "token is null");
                            else {
                                Log.i(TAG, token);
                                // 토큰 저장

                                setString(mContext,"token",token);
                                showToken(layout);
                            }
                        } else {
                            Log.i(TAG, error);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                },uiHandler)
        );

        // 일반 쿼리 사용예
        apolloClient.query(allMembersQuery).enqueue(
                new ApolloCallback<AllMembersQuery.Data>(new ApolloCall.Callback<AllMembersQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<AllMembersQuery.Data> response) {
                        Log.i(TAG, response.toString());
                        if (response.data()!=null) {
                            int size = response.data().allMembers().size();
                            Log.i(TAG, "size : " + size);
                            if (size > 0) {
                                for (AllMembersQuery.AllMember member:
                                        response.data().allMembers()) {
                                    Log.i(TAG, "member : " + member.email() +" "+ member.id());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                },uiHandler)
        );

    }

}
