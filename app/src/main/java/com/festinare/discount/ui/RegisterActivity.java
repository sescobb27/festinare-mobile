package com.festinare.discount.ui;

import com.festinare.discount.tools.SessionHelper;
import com.google.gson.Gson;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.festinare.discount.models.User;
import com.festinare.discount.R;
import com.festinare.discount.tools.ConnectionDetector;
import com.festinare.discount.tools.http.AuthHelper;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etxtEmail;
    private EditText etxtUsername;
    private EditText etxtPassword;
    private EditText etxtPasswordConfirmation;
    private Button btnRegister;
    private ProgressBar pbRegister;
    private AuthHelper auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup();
    }

    private void setup() {
        etxtEmail = (EditText) findViewById(R.id.etxtEmailRegister);
        etxtUsername = (EditText) findViewById(R.id.etxtUsernameRegister);
        etxtPassword = (EditText) findViewById(R.id.etxtPasswordRegister);
        etxtPasswordConfirmation = (EditText) findViewById((R.id.etxtPasswordConfirmationRegister));
        pbRegister = (ProgressBar) findViewById(R.id.pbRegister);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        auth = new AuthHelper();
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidPassword(String password) {

        String passwordConfirmation = etxtPasswordConfirmation.getText().toString().trim();

        if (password.length() < 8 ) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_password_length), Toast.LENGTH_LONG).show();
            return false;
        }

        if(passwordConfirmation.isEmpty() || !password.equals(passwordConfirmation)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_password_confirmation), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Verifies that the email is well written
     */
    public boolean isEmail(String email) {
        if (!email.isEmpty()) {
            Pattern pat;
            Matcher mat;
            pat = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$");
            mat = pat.matcher(email);
            if (mat.find()) {
                return true;
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isFieldsEmpty(String email, String username, String password) {

        if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty()){
            return true;
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.error_blankSpace_all), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public void register() {

        String email = etxtEmail.getText().toString().trim();
        String username = etxtUsername.getText().toString().trim();
        String password = etxtPassword.getText().toString().trim();

        if (isFieldsEmpty(email, username, password) && isEmail(email) && isValidPassword(password)) {
            setButtonState(false);
            try {
                auth.register(
                        getApplicationContext(),
                        email,
                        username,
                        password,
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Gson gson = new Gson();
                                Log.i("REGISTER USER: ", response.toString());
                                try {
                                    JSONObject tmp = response.getJSONObject("user");
                                    gson.fromJson(tmp.toString(), User.class);
                                    SessionHelper sessionHelper = new SessionHelper(getApplicationContext());
                                    sessionHelper.setUser(tmp.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_register), Toast.LENGTH_LONG).show();
                                    setButtonState(true);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);

                                Toast.makeText(getApplicationContext(), getString(R.string.error_register), Toast.LENGTH_LONG).show();
                                setButtonState(true);
                            }
                        });
            } catch (JSONException | UnsupportedEncodingException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.error_register), Toast.LENGTH_LONG).show();
                setButtonState(true);
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                ConnectionDetector connDetector = ConnectionDetector.getConnectionDetector(getApplicationContext());
                if (connDetector.isConnectedToInternet()) {
                    register();
                }else {
                    Log.i("FestinareDiscount", "No internet Connection");
                }
                break;
        }
    }

    public void setButtonState(boolean enabled) {

        if(enabled){
            pbRegister.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
        }else{
            pbRegister.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
        }
    }
}