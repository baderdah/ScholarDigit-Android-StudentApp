package com.ensas.myapplication.backgroundTask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import androidx.annotation.RequiresApi;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.ui.profil.ProfilActivity;
import com.ensas.myapplication.util.MyCertifications;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class AsyncProfil extends AsyncTask<URL, Void, String> {

    ProfilActivity activity;

    public AsyncProfil(ProfilActivity activity){
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
            queryResult = RestController.MyCertifications(url, activity.getMyToken());
        } catch (IOException e) {
            e.printStackTrace();
            queryResult = null;
        }
        return queryResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(String s) {
        //Log.d("ASYNC", "onPostExecute: ");
        if (s != null) {
            Log.d("AsyncPost", s);

            try {
                MyCertifications myCertifications = new MyCertifications(s);
                activity.buildRecycler(s);
                activity.getProgressBar().setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                Log.d("Exception parsing", "parsing.....error");
                activity.finish();
            }
        } else {
            Log.d("AsyncPost", "null result");
            activity.finish();
        }
    }


}
