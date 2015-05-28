package com.festinare.discount.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.festinare.discount.R;
import com.festinare.discount.models.Category;
import com.festinare.discount.models.User;
import com.festinare.discount.tools.ConnectionDetector;
import com.festinare.discount.tools.SessionHelper;
import com.festinare.discount.tools.gcm.GCMRegistrationHelper;
import com.festinare.discount.tools.http.AuthHelper;
import com.festinare.discount.tools.http.UserHelper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoriesPickerFragment.CategoriesPickerListener {

    private SessionHelper sessionHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();
        sessionHelper = new SessionHelper(getApplicationContext());
        user = gson.fromJson(sessionHelper.getUser(), User.class);

        GCMRegistrationHelper gcmRegistrationHelper = new GCMRegistrationHelper(MainActivity.this, user);
        gcmRegistrationHelper.registerGCM();

        ConnectionDetector connDetector = ConnectionDetector.getConnectionDetector(getApplicationContext());
        if (connDetector.isConnectedToInternet()) {
            checkLogin(sessionHelper.getAPIToken());
        }
        if(user.getCategories().isEmpty()){
            DialogFragment categoriesPickerFragment = new CategoriesPickerFragment();
            categoriesPickerFragment.show(getSupportFragmentManager(), "timePicker");
        }

        UserHelper userHelper = new UserHelper();

        userHelper.getDiscounts(this,
        new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Discounts: ", response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_settings_main:
                return true;

            case R.id.action_logout:
                sessionHelper.logOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in the account specified by stored token.
     */
    private void checkLogin(String token) {

        AuthHelper auth = new AuthHelper();

        auth.me(getApplicationContext(), token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONObject tmp = response.getJSONObject("user");
                    SessionHelper sessionHelper = new SessionHelper(getApplicationContext());
                    if(!tmp.toString().equals(sessionHelper.getUser())) {
                        sessionHelper.setUser(tmp.toString());
                        Gson gson = new Gson();
                        user = gson.fromJson(tmp.toString(), User.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("onFailure","failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("onFailure", "fails");
                Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_login), Toast.LENGTH_LONG).show();
                sessionHelper.logOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    @Override
    public void onCategoriesSelected(DialogFragment dialog, final List<Category> categories) {

        UserHelper userHelper = new UserHelper();
        try {
            userHelper.setCategories(getApplicationContext(), user, categories, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    user.setCategories(categories);
                    Gson gson = new Gson();
                    SessionHelper sessionHelper = new SessionHelper(getApplicationContext());
                    sessionHelper.setUser(gson.toJson(user));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("Categories update", "", error);
                }

            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }
}
