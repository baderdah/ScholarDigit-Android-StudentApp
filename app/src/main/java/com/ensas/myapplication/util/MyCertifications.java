package com.ensas.myapplication.util;

import android.content.SharedPreferences;
import android.os.Build;
import androidx.annotation.RequiresApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyCertifications {
    String jsonResult;
    JSONArray requests;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MyCertifications(String result) throws JSONException {
        jsonResult = result;
        requests = new JSONArray(jsonResult);
        orderCertification();

    }
    public void show() throws JSONException {
        for(int i = 0; i < requests.length(); i++ ){
            JSONObject o = requests.getJSONObject(i);
            System.out.println(o.get("id"));
            //System.out.println(o.get("gender"));
        }
    }

    public int nbRequests(){return  requests.length();}

    public JSONObject get(int i) throws JSONException {
        return requests.getJSONObject(i);
    }

    public Boolean anyOneReady() throws JSONException {
        for(int i = 0; i < requests.length(); i++ ){
            JSONObject o = requests.getJSONObject(i);
            if((Boolean) o.get("ready")){
                return true;
            }
        }
        return false;
    }

    public int getReadyOneById(SharedPreferences sharedPreferences) throws JSONException {
        for(int i = 0; i < requests.length(); i++ ){
            JSONObject o = requests.getJSONObject(i);
            int id = (int)o.get("id");
            boolean ready = (Boolean) o.get("ready");
            if( ready && sharedPreferences.getInt("id"+id, 0) == 0){
                return id;
            }
        }
        return 0;
    }

    public void switchElements(int i, int j) throws JSONException {
        JSONObject tmp = requests.getJSONObject(j);
        requests.put(j, requests.getJSONObject(i));
        requests.put(i, tmp);
    }

    public void orderCertification() throws JSONException {
        for(int i = 0; i < requests.length(); i++ ){
            JSONObject o = requests.getJSONObject(i);
            int id = (int)o.get("id");
            for(int j = i; j < requests.length(); j++){
                if((int)requests.getJSONObject(j).get("id") > id){
                    switchElements(i, j);
                }
            }
        }
    }

}
