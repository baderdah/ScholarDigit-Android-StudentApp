package com.ensas.myapplication.ui.profil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import com.ensas.myapplication.R;
import com.ensas.myapplication.backgroundTask.AsyncRequest;
import com.ensas.myapplication.rest.RestController;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class CertifRequest extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    String myToken;
    Button submit;
    RadioGroup radioGroup;
    RadioButton radioButtonScl;
    RadioButton radioButtonSuc;
    static final int SEARCH_LOADER = 22;
    int typeCertif = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certif_request);
        radioButtonScl = findViewById(R.id.radioButtonScholarity);
        radioButtonSuc = findViewById(R.id.radioButtonSuccess);
        submit = findViewById(R.id.submit);
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            myToken = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }
        getSupportLoaderManager().initLoader(SEARCH_LOADER, null, this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               makeRequest();

            }
        });
    }


    public void makeRequest(){
        URL url = RestController.buildUrl("newCertificationRequest", "");
        System.out.println(url.toString());
        Log.d("TAG", url.toString());
        Bundle queryBundle = new Bundle();
        queryBundle.putString("url", url.toString());
        queryBundle.putInt("type", typeCertif);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> SearchLoader = loaderManager.getLoader(SEARCH_LOADER);
        if (SearchLoader == null) {
            loaderManager.initLoader(SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(SEARCH_LOADER, queryBundle, this);
        }
    }

    public String getMyToken() {
        return myToken;
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        // COMPLETED (4) Return a new AsyncTaskLoader<String> as an anonymous inner class with this as the constructor's parameter
        return new AsyncRequest(args, this);
    }

    @SuppressLint("ShowToast")
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if(data == null || !data.equals("200") ){
            System.out.println("no data found");
            Toast.makeText(CertifRequest.this, "Request wasn't successful", Toast.LENGTH_LONG).show();
        }
        else {
            System.out.println(data);
            Toast.makeText(CertifRequest.this, "Request has been made successfully", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonScholarity:
                if (checked)
                    typeCertif = 0;
                    break;
            case R.id.radioButtonSuccess:
                if (checked)
                    typeCertif = 1;
                    break;
        }

    }
}
