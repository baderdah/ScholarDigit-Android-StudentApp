package com.ensas.myapplication.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.ensas.myapplication.R;
import com.ensas.myapplication.backgroundTask.AsyncLogin;
import com.ensas.myapplication.rest.RestController;
import com.ensas.myapplication.sync.ReminderUtilities;
import com.ensas.myapplication.ui.profil.ProfilActivity;

import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    public ProgressBar getLoadingProgressBar() {
        return loadingProgressBar;
    }

    public void setLoadingProgressBar(ProgressBar loadingProgressBar) {
        this.loadingProgressBar = loadingProgressBar;
    }

    public EditText getUsernameEditText() {
        return usernameEditText;
    }

    public void setUsernameEditText(EditText usernameEditText) {
        this.usernameEditText = usernameEditText;
    }

    public EditText getPasswordEditText() {
        return passwordEditText;
    }

    public void setPasswordEditText(EditText passwordEditText) {
        this.passwordEditText = passwordEditText;
    }

    private LoginViewModel loginViewModel;
    private ProgressBar loadingProgressBar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMe;
    public static final String mypreference = "myAppPref";
    public SharedPreferences sharedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMe);
        final Button loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        checkCredentials();
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        if(!passwordEditText.getText().toString().equals("")){
            loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredentials();
                Log.d("LoginActivity", "onClick: ");
                loadingProgressBar.setVisibility(View.VISIBLE);
                URL url = RestController.buildUrl("authenticate", null);
                Log.d("LoginActivity", url.toString());
                //AsyncQuery myQuery = new AsyncQuery();
                AsyncLogin myQuery = new AsyncLogin(LoginActivity.this);
                myQuery.execute(url);
                if(myQuery.isCancelled()){
                    Log.d("LoginActivity", "cancelled");
                }

                /*loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());*/
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome);
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void checkCredentials(){
        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        String mail = sharedPreferences.getString("mail","");
        String pass = sharedPreferences.getString("password","");
        String token = sharedPreferences.getString("myToken", "");
        passwordEditText.setText(pass);
        usernameEditText.setText(mail);
        //ReminderUtilities.scheduleReminder(this);

        if(!token.equals("")){
            Intent intent = new Intent(this, ProfilActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, token);
            startActivity(intent);
        }

    }

    public void saveCredentials(){
        if(rememberMe.isChecked()){
            String mail = sharedPreferences.getString("mail","");
            String pass = sharedPreferences.getString("password","");
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString("mail", usernameEditText.getText().toString());
                myEdit.putString("password", passwordEditText.getText().toString());
                myEdit.apply();

        }

    }

}
