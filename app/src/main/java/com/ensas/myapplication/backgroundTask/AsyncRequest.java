package com.ensas.myapplication.backgroundTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import androidx.loader.content.AsyncTaskLoader;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.ui.profil.CertifRequest;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class AsyncRequest extends AsyncTaskLoader<String> {

    Bundle args;
    CertifRequest activity;
    public AsyncRequest(Bundle args, CertifRequest activity){
        super(activity);
        this.activity = activity;
        this.args = args;
    }
    @Override
    protected void onStartLoading() {
        if (args == null) {
            return;
        }

        forceLoad();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public String loadInBackground() {

        String searchQueryUrlString = args.getString("url");
        int type = args.getInt("type");

        if (TextUtils.isEmpty(searchQueryUrlString)) {
            return null;
        }

        try {
            URL url = new URL(searchQueryUrlString);
            String githubSearchResults = RestController.newCertification(url, activity.getMyToken(), type);
            return githubSearchResults;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
