package com.festinare.discount.ui;

import com.festinare.discount.tools.ProfileQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.festinare.discount.models.User;
import com.festinare.discount.R;
import com.festinare.discount.tools.ConnectionDetector;
import com.festinare.discount.tools.http.AuthHelper;


public class RegisterActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private AutoCompleteTextView emailView;
    private EditText usernameView;
    private EditText passwordView;
    private EditText passwordConfirmationView;
    private View progressView;
    private View registerFormView;
    private Button registerButton;
    private AuthHelper auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        if (checkPlayServices()) {
            ConnectionDetector connDetector = ConnectionDetector
                    .getConnectionDetector(getApplicationContext());
            if (connDetector.isConnectedToInternet()) {
                setup();
            } else {
                Log.i("FestinareDiscount", "Activa los datos o conectate al wifi mas cercano");
                Toast.makeText(getApplicationContext(),
                        "Activa los datos o conectate al wifi mas cercano",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }else{
            Log.e("GCM", "No se ha encontrado Google Play Services.");
        }
    }

    private void setup() {
        emailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        populateAutoComplete();
        usernameView = (EditText) findViewById(R.id.register_username);
        passwordView = (EditText) findViewById(R.id.register_password);
        passwordConfirmationView = (EditText) findViewById((R.id.register_password_confirmation));
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        auth = new AuthHelper();
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
                Log.e("FestinareDiscount", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean isValidPassword() {
        String password = passwordView.getText().toString();
        String passwordConfirmation = passwordConfirmationView.getText().toString();
        return !password.isEmpty() &&
                password.length() >= 1 &&
                !passwordConfirmation.isEmpty() &&
                password.equals(passwordConfirmation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                if (isValidPassword()) {
                    try {
                        auth.register(
                            getApplicationContext(),
                            emailView.getText().toString(),
                            usernameView.getText().toString(),
                            passwordView.getText().toString(),
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Gson gson = new Gson();
                                    android.util.Log.i("REGISTER USER: ", response.toString());
                                    try {
                                        JSONObject tmp = response.getJSONObject("user");
                                        user = gson.fromJson(tmp.toString(), User.class);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent
                                                        intent = new Intent(RegisterActivity.this, MainActivity.class);
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
                    } catch (JSONException e) {
                        // TODO
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        // TODO
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
