package com.pixelro.graphqlsample;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JWTUtils {

    public static String decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJsonString(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJsonString(split[1]));
            String value= "Header: " + getJsonString(split[0]);
            value+= "\n\nBody: " + getJsonString(split[1]);
            return value;
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return  "";
    }

    public static String getJsonString(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    public static JSONObject getJson(String token, String key) throws UnsupportedEncodingException{
        try {
            if (token==null || key==null || "".equals(key) || "".equals(token))
                return null;

            String[] split = token.split("\\.");
            if (split.length != 3)
                return null;

            Log.d("JWT_DECODED", "Body: " + split[1]);
            String body = getJsonString(split[1]);
            JSONObject jsonObjAll = new JSONObject(body);
            JSONObject jsonObj = (JSONObject) jsonObjAll.get(key);
            return  jsonObj;
        } catch ( JSONException e) {
            //Error
            Log.i("",e.getMessage());
        }
        return null;
    }
}