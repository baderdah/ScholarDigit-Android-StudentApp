package com.ensas.myapplication.backgroundTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.ui.login.LoginActivity;
import com.ensas.myapplication.ui.profil.ProfilActivity;
import com.ensas.myapplication.util.MyCertifications;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class AsyncLogin extends AsyncTask<URL, Void, String> {
    LoginActivity activity;

    public AsyncLogin(LoginActivity activity){
        this.activity = activity;
    }

    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(URL... urls) {
        //Log.d("ASYNC", "doInBackground: ");
        URL url = urls[0];
        String queryResult;
        try {
            queryResult = RestController.loginTo(url, activity.getUsernameEditText().getText().toString(),
                    activity.getPasswordEditText().getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            queryResult = null;
        }
        return queryResult;
    }

    @Override
    protected void onPostExecute(String s) {
        //Log.d("ASYNC", "onPostExecute: ");
        activity.getLoadingProgressBar().setVisibility(View.INVISIBLE);
        if(s != null){
            Log.d("AsyncPost", s);
            SharedPreferences.Editor myEdit = activity.sharedPreferences.edit();
            System.out.println("saving the token");
            myEdit.putString("myToken", s);
            myEdit.apply();
            Intent intent = new Intent(activity, ProfilActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, s);
            activity.startActivity(intent);
        }
        else {
            Log.d("AsyncPost", "null result");
            Toast.makeText(activity, "Sorry, the provided credentials don't exist", Toast.LENGTH_LONG).show();
        }
    }


}
