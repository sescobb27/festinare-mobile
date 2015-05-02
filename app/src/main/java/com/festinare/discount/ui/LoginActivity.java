package com.festinare.discount.ui;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import com.festinare.discount.models.User;
import com.festinare.discount.R;
import com.festinare.discount.tools.ConnectionDetector;
import com.festinare.discount.tools.http.AuthHelper;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // UI references.
    private EditText etxtUsername;
    private EditText etxtPassword;
    private Button btnLogin;
    private Button btnRegister;
    private ProgressBar pbLogin;
    private AuthHelper auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        //TODO This code is going to be in IndexActivity
        if (checkPlayServices()) {
            //ConnectionDetector connDetector = ConnectionDetector.getConnectionDetector(getApplicationContext());
            //if (connDetector.isConnectedToInternet()) {
                auth = new AuthHelper();
//                SessionHelper sessionHelper = new SessionHelper(getApplicationContext());
//                String token = sessionHelper.getAPIToken();
//                if (!token.isEmpty()) {
//                    loginByToken(token);
//                }else{
                    setup();
                //}
//            } else {
//                Log.i("FestinareDiscount", "No internet Connection");
//                //finish();
//            }
        }
    }

    private void setup() {
        // Set up the login form.
        btnRegister = (Button) findViewById(R.id.btnRegisterLogin);
        btnRegister.setOnClickListener(this);

        etxtUsername = (EditText) findViewById(R.id.etxtUsernameLogin);
        etxtPassword = (EditText) findViewById(R.id.etxtPasswordLogin);

        pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("FestinareDiscount", "There is not Google Play Services, This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Attempts to sign in the account specified by stored token.
     */
    private void loginByToken(String token) {
        auth.me(getApplicationContext(), token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Log.i("LOGIN USER: ", response.toString());
                try {
                    JSONObject tmp = response.getJSONObject("user");
                    user = gson.fromJson(tmp.toString(), User.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    // TODO
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                // TODO
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     */
    private void login() {

        // Store values at the time of the login attempt.
        String username = etxtUsername.getText().toString().trim();
        String password = etxtPassword.getText().toString().trim();

        // Check for a valid password, if the user entered one.
        if (!password.isEmpty() && !username.isEmpty()) {
            setButtonState(false);
            try {
                auth.login(
                    getApplicationContext(),
                    etxtUsername.getText().toString(),
                    etxtPassword.getText().toString(),
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //setProgressBarIndeterminateVisibility(false);
                            Gson gson = new Gson();
                            Log.i("LOGIN USER: ", response.toString());
                            try {
                                JSONObject tmp = response.getJSONObject("user");
                                user = gson.fromJson(tmp.toString(), User.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
                                setButtonState(true);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            //setProgressBarIndeterminateVisibility(false);
                            if(statusCode==401){
                                Toast.makeText(getApplicationContext(), getString(R.string.error_login_wrong_data), Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
                            }

                            setButtonState(true);
                        }
                    });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
                setButtonState(true);
            }
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.error_blankSpace_all), Toast.LENGTH_LONG).show();
        }
        //setProgressBarIndeterminateVisibility(false);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegisterLogin:
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity( registerIntent );
                finish();
                break;
            case R.id.btnLogin:
                ConnectionDetector connDetector = ConnectionDetector.getConnectionDetector(getApplicationContext());
                if (connDetector.isConnectedToInternet()) {
                    login();
                }else {
                    Log.i("FestinareDiscount", "No internet Connection");
                }
                break;
        }
    }

    public void setButtonState(boolean enabled) {

        if(enabled){
            pbLogin.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
        }else{
            pbLogin.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        }
    }
}