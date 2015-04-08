package discount.up.hurry.hurryupdiscount.Auth;

import com.google.gson.Gson;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import discount.up.hurry.hurryupdiscount.Activities.MainActivity;
import discount.up.hurry.hurryupdiscount.Models.User;
import discount.up.hurry.hurryupdiscount.R;
import discount.up.hurry.hurryupdiscount.Services.ConnectionDetectorService.ConnectionDetectorService;
import discount.up.hurry.hurryupdiscount.Services.HTTPService.AuthService;
import discount.up.hurry.hurryupdiscount.Services.SessionService.SessionService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements OnClickListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private AuthService auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Context currentContext = getApplicationContext();

        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        if (checkPlayServices()) {
            ConnectionDetectorService connDetector = ConnectionDetectorService.getConnectionDetector(getApplicationContext());
            if (connDetector.isConnectedToInternet()) {
                auth = new AuthService();
                SessionService sessionService = new SessionService(getApplicationContext());
                String token = sessionService.getAPIToken();
                if (!token.isEmpty()) {
                    loginByToken(token);
                } else {
                    setup();
                }
            } else {
                android.util.Log.i("HurryUpDiscount", "Activa los datos o conectate al wifi mas cercano");
                Toast.makeText(currentContext, "Activa los datos o conectate al wifi mas cercano", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void setup() {
        // Set up the login form.
        Button mRegisterButton = (Button) findViewById(R.id.login_register_button);
        mRegisterButton.setOnClickListener(this);

        mUsernameView = (EditText) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        Button mEmailSignInButton = (Button) findViewById(R.id.login_button);
        mEmailSignInButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
        int resultCode = com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable(
                this);
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            if (com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                android.util.Log.i("HurryUpDiscount", "This device is not supported.");
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
                android.util.Log.i("LOGIN USER: ", response.toString());
                user = gson.fromJson(response.toString(), User.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }
                });
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
    private void loginByCredentials() {
        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (isPasswordValid(password) && !username.isEmpty()) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            try {
                auth.login(
                    getApplicationContext(),
                    mUsernameView.getText().toString(),
                    mPasswordView.getText().toString(),
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Gson gson = new Gson();
                            android.util.Log.i("LOGIN USER: ", response.toString());
                            user = gson.fromJson(response.toString(), User.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }


                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            // TODO
                        }
                    });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty() && password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register_button:
                Intent registerIntent = new Intent( getApplicationContext(), RegisterActivity.class );
                startActivity( registerIntent );
                finish();
                break;
            case R.id.login_button:
                loginByCredentials();
                break;
        }
    }

}



