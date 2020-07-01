package com.ensas.myapplication.rest;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;
import androidx.annotation.RequiresApi;
import okhttp3.*;
import okhttp3.internal.http2.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RestController {
    final static String REST_BASE_URL ="http://192.168.1.5:8080";

    @SuppressLint("LongLogTag")
    public static URL buildUrl(String newPath, String token) {
        Uri builtUri = null;
        if(token != null){
            builtUri = Uri.parse(REST_BASE_URL).buildUpon()
                    .appendPath("student")
                    .appendPath(newPath)
                    .appendPath(token)
                    .build();
        }

        else{
            builtUri = Uri.parse(REST_BASE_URL).buildUpon()
                    .appendPath(newPath)
                    .build();
        }


        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.d("---------URL malformed----->", "buildUrl: failed ");
            e.printStackTrace();
        }

        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String loginTo(URL url, String mail, String password) throws JSONException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.accumulate("userName",mail);
        json.accumulate("password", password);

        Request request = new Request.Builder().addHeader("Content-Type", "application/json")
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String MyCertifications(URL url, String token) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+ token)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
        catch (Exception e){
            return "null";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String newCertification(URL url, String token, int type) throws JSONException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.accumulate("typeCertificat",type);
        json.accumulate("jwt", token);

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+ token)
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getToken(String tokenJson){
        JSONObject parser = null;
        try {
            parser = new JSONObject(tokenJson);
            tokenJson = parser.get("jwt").toString();
            return tokenJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
