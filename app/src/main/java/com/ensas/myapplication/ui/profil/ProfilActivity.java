package com.ensas.myapplication.ui.profil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.ensas.myapplication.R;
import com.ensas.myapplication.backgroundTask.AsyncProfil;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.sync.ReminderUtilities;
import com.ensas.myapplication.ui.login.LoginActivity;
import com.ensas.myapplication.util.GreenAdapter;
import com.ensas.myapplication.util.HelperUtil;
import com.ensas.myapplication.util.MyCertifications;
import com.ensas.myapplication.util.NotificationUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class ProfilActivity extends AppCompatActivity implements GreenAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    String myToken;
    private GreenAdapter mAdapter;
    private RecyclerView mCertificateList;
    private URL myUrl;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        ReminderUtilities.scheduleReminder(this);
        Intent intentThatStartedThisActivity = getIntent();
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.refresher);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String tokenJson = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            JSONObject parser = null;
            try {
                parser = new JSONObject(tokenJson);
                tokenJson = parser.get("jwt").toString();
                myToken = tokenJson;
                myUrl = RestController.buildUrl("myCertifications", tokenJson);
                System.out.println(myUrl);
                refreshRecycler();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilActivity.this, CertifRequest.class);
                intent.putExtra(Intent.EXTRA_TEXT, myToken);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onListItemClick(int clickedItemIndex) throws JSONException {
        String response = "Not ready yet...";
        if(mAdapter.getState(clickedItemIndex)){
            response = "Your request is ready";
        }
        Toast.makeText(this, response, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void buildRecycler(String data){
        try {
            System.out.println("before inflating rv");
            mCertificateList =(RecyclerView) findViewById(R.id.rv_certificates);
            System.out.println("aftre inflating rv");
            mCertificateList.setHasFixedSize(true);
            LinearLayoutManager linearLayout = new LinearLayoutManager(this);
            mCertificateList.setLayoutManager(linearLayout);
            mAdapter = new GreenAdapter(data, this, ProfilActivity.this);
            mCertificateList.setAdapter(mAdapter);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecycler();
    }

    private void refreshRecycler() {
        mAdapter = null;
        new AsyncProfil(this).execute(myUrl);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                swipeRefreshLayout.setRefreshing(true);
                refreshRecycler();
                break;
            case R.id.logoff:
                HelperUtil.removeToken(this);
                finish();
                break;
            case R.id.contactUs:
                NotificationUtils.composeEmail(ProfilActivity.this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public String getMyToken() {
        return myToken;
    }

    public void setMyToken(String myToken) {
        this.myToken = myToken;
    }

    public GreenAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(GreenAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    @Override
    public void onRefresh() {
        Log.i("On Refresh", "onRefresh called from SwipeRefreshLayout");
        refreshRecycler();
    }
}
