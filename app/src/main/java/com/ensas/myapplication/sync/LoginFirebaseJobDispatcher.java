package com.ensas.myapplication.sync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.ui.login.LoginActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import org.json.JSONException;

import java.net.URL;

public class LoginFirebaseJobDispatcher extends JobService {
    AsyncTask<URL, Void, String> mAsyncLogin;
    AsyncRequests mAsyncRequests;
    final URL url = RestController.buildUrl("authenticate", null);
    String myToken = null;
    URL myUrl;
    SharedPreferences sharedPreferences;
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mAsyncLogin = new AsyncTask<URL, Void, String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(URL... urls) {
                URL url = urls[0];
                String queryResult;
                sharedPreferences = getSharedPreferences(LoginActivity.mypreference,
                        Context.MODE_PRIVATE);
                String mail = sharedPreferences.getString("mail","");
                String pass = sharedPreferences.getString("password","");
                String token = sharedPreferences.getString("myToken","");
                System.out.println("the mail" + mail);
               /* try {

                    queryResult = RestController.loginTo(url, mail,
                            pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                    queryResult = null;
                }*/
                return token;
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("query result ====================> ");
                System.out.println(s);
                if(s!= null){
                    if(!s.equals("")){
                        myToken = ReminderUtilities.getToken(s);
                        myUrl = RestController.buildUrl("myCertifications", myToken);
                        mAsyncRequests = new AsyncRequests(myToken, LoginFirebaseJobDispatcher.this);
                        mAsyncRequests.execute(myUrl);
                    }

                }
                else {
                    System.out.println("the token "+ s + " is timed out");
                }

                jobFinished(jobParameters, false);

            }
        };
        mAsyncLogin.execute(url);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mAsyncLogin != null) mAsyncLogin.cancel(true);
        return true;
    }
}
