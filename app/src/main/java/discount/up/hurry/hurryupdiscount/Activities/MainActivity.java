package discount.up.hurry.hurryupdiscount.Activities;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.UnsupportedEncodingException;

import discount.up.hurry.hurryupdiscount.Models.Mobile;
import discount.up.hurry.hurryupdiscount.Models.User;
import discount.up.hurry.hurryupdiscount.R;
import discount.up.hurry.hurryupdiscount.Services.GCMService.GCMRegistrationService;
import discount.up.hurry.hurryupdiscount.Services.GCMService.OnGCMRegister;
import discount.up.hurry.hurryupdiscount.Services.HTTPService.UserService;

public class MainActivity extends ActionBarActivity implements OnGCMRegister {


    private GCMRegistrationService gcmRegistrationService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (User) getIntent().getSerializableExtra("user");

        gcmRegistrationService = new GCMRegistrationService(getApplicationContext(), this);
        gcmRegistrationService.getGcmRegistrationIdOrRegister();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGCMRegister(Mobile mobile) {
        if ( gcmRegistrationService.needsGCMKeyUpdate() ) {
            UserService userService = new UserService();
            try {
                userService.mobile(getApplicationContext(), user, mobile, new JsonHttpResponseHandler(
                        HTTP.UTF_8) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        // GOTO MAIN_ACTIVITY
                        android.util.Log.i("onGCMRegister", "SUCCESS");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                        // TODO
                        android.util.Log.e(AsyncHttpClient.LOG_TAG, error.getMessage());
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
    }

}