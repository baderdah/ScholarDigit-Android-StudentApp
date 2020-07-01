package com.ensas.myapplication.sync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.ui.login.LoginActivity;
import com.ensas.myapplication.util.MyCertifications;
import com.ensas.myapplication.util.NotificationUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class AsyncRequests extends AsyncTask<URL, Void, String> {
    String myToken;
    Context context;
    AsyncTask mAsyncTask;
    SharedPreferences sharedPreferences;

    public AsyncRequests(String token, Context context){
        this.myToken = token;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(LoginActivity.mypreference,
                Context.MODE_PRIVATE);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(URL... urls) {
        if(myToken != null){
            URL myUrl = urls[0];
            System.out.println("Getting request .... in back");
            String queryResult;
            try {
                queryResult = RestController.MyCertifications(myUrl, myToken);
                System.out.println("Query result " +queryResult);
            } catch (IOException e) {
                e.printStackTrace();
                queryResult = null;
            }
            return queryResult;
        }
       return null;
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(final String s) {
        if(s != null){
            System.out.println(s);
            mAsyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        MyCertifications myCertifications = new MyCertifications(s);
                        int prev = 0;
                        if(myCertifications.anyOneReady()){
                            prev = myCertifications.getReadyOneById(sharedPreferences);
                            if(prev != 0){
                                System.out.println("A new ready one...............");
                                NotificationUtils.remindUserBecauseCharging(context);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putInt("id"+prev, prev);
                                myEdit.apply();
                            }
                            else {
                                System.out.println("already seen that one");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            mAsyncTask.execute();

        }
        else {
            System.out.println("no request on post null");
        }
    }
}
