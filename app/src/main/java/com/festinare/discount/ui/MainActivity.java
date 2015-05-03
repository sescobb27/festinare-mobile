package com.festinare.discount.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.festinare.discount.R;
import com.festinare.discount.models.User;
import com.festinare.discount.tools.SessionHelper;
import com.festinare.discount.tools.gcm.GCMRegistrationHelper;
import com.festinare.discount.tools.http.AuthHelper;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{

    private SessionHelper sessionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();
        sessionHelper = new SessionHelper(getApplicationContext());
        User user = gson.fromJson(sessionHelper.getUser(), User.class);

        GCMRegistrationHelper gcmRegistrationHelper = new GCMRegistrationHelper(MainActivity.this, user);
        gcmRegistrationHelper.registerGCM();

        checkLogin(sessionHelper.getAPIToken());
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

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("onFailure","failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("onFailure","fails");
                sessionHelper.logOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}
