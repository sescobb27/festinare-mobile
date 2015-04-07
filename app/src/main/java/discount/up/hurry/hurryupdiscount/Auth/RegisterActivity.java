package discount.up.hurry.hurryupdiscount.Auth;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
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

import discount.up.hurry.hurryupdiscount.R;
import discount.up.hurry.hurryupdiscount.Services.ConnectionDetectorService.ConnectionDetectorService;
import discount.up.hurry.hurryupdiscount.Services.HTTPService.AuthAsyncTask;


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

    private AuthAsyncTask auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        if (checkPlayServices()) {
            ConnectionDetectorService connDetector = ConnectionDetectorService
                    .getConnectionDetector(getApplicationContext());
            if (connDetector.isConnectedToInternet()) {
                setup();
            } else {
                android.util.Log
                        .i("HurryUpDiscount", "Activa los datos o conectate al wifi mas cercano");
                Toast.makeText(getApplicationContext(),
                        "Activa los datos o conectate al wifi mas cercano",
                        Toast.LENGTH_LONG).show();
                finish();
            }
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

        auth = new AuthAsyncTask();
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
        int resultCode = com.google.android.gms.common.GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            if (com.google.android.gms.common.GooglePlayServicesUtil
                    .isUserRecoverableError(resultCode)) {
                com.google.android.gms.common.GooglePlayServicesUtil
                        .getErrorDialog(resultCode, this,
                                PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                android.util.Log.i("HurryUpDiscount", "This device is not supported.");
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
        return !password.isEmpty() && !passwordConfirmation.isEmpty() && password
                .equals(passwordConfirmation);
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
                                    public void onSuccess(int statusCode, Header[] headers,
                                            JSONObject response) {
                                        android.util.Log.i("USER: ", response.toString());
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
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