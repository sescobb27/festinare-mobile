package discount.up.hurry.hurryupdiscount.Services.HTTPService;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import discount.up.hurry.hurryupdiscount.Services.SessionService.SessionService;

public class AuthAsyncTask extends JsonHttpResponseHandler {

    private AsyncHttpClient client;
    private AsyncHttpResponseHandler callback;
    private Context callbackContext;

    public AuthAsyncTask() {
        super(HTTP.UTF_8);
        client = new AsyncHttpClient();
        client.addHeader(AsyncHttpClient.HEADER_ACCEPT_ENCODING, AsyncHttpClient.ENCODING_GZIP);
    }

    public void login(Context context, String username, String password, AsyncHttpResponseHandler responseHandler)
            throws JSONException, UnsupportedEncodingException {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        callback = responseHandler;
        callbackContext = context;
        ByteArrayEntity entity = new ByteArrayEntity(body.toString().getBytes(HTTP.UTF_8));
        client.post(context, HTTPCommons.LOGIN_URL, null, entity, "application/json" , this);
    }

    public void register(Context context, String email, String username, String password, AsyncHttpResponseHandler responseHandler)
            throws JSONException, UnsupportedEncodingException {
        JSONObject body = new JSONObject();
        body.put("email", email);
        body.put("username", username);
        body.put("password", password);
        callback = responseHandler;
        callbackContext = context;
        ByteArrayEntity entity = new ByteArrayEntity(body.toString().getBytes(HTTP.UTF_8));
        client.post(context, HTTPCommons.REGISTER_URL, null, entity, "application/json" , this);
    }

    public void me(Context context, String token, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + token);
        client.post(context, HTTPCommons.ME_URL, null, responseHandler);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        if (statusCode == HttpStatus.SC_OK) {
//            response contains the authorization token
            try {
                String token = response.getString("token");
                SessionService session = new SessionService(callbackContext);
                session.setAPIToken(token);
                me(callbackContext, token, callback);
            } catch (JSONException e) {
                android.util.Log.e(AsyncHttpClient.LOG_TAG, e.toString());
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
        android.util.Log.e(AsyncHttpClient.LOG_TAG, error.getMessage());
    }
}

