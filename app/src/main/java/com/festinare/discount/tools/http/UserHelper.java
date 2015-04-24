package com.festinare.discount.tools.http;

import com.festinare.discount.tools.SessionHelper;
import com.google.gson.Gson;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.festinare.discount.models.Mobile;
import com.festinare.discount.models.User;

public class UserHelper {

    private AsyncHttpClient client;

    public UserHelper() {
        client = new AsyncHttpClient();
        client.addHeader(AsyncHttpClient.HEADER_ACCEPT_ENCODING, AsyncHttpClient.ENCODING_GZIP);
    }

    public void mobile (Context context, User user, Mobile mobile, AsyncHttpResponseHandler responseHandler)
            throws JSONException, UnsupportedEncodingException {
        Gson gson = new Gson();
        String mobileJsonContent = gson.toJson(mobile);
        JSONObject mobileJson = new JSONObject();
        mobileJson.put("mobile",mobileJsonContent);
        JSONObject userJson = new JSONObject();
        userJson.put("user", mobileJson);

        Map<String, String> urlValues = new HashMap<>();
        urlValues.put("id", user.getId());
        String url = HTTPCommons.replaceUrlPlaceholders(HTTPCommons.USER_SET_MOBILE_URL, urlValues);
        ByteArrayEntity entity = new ByteArrayEntity(userJson.toString().getBytes(HTTP.UTF_8));
        SessionHelper session = new SessionHelper(context);
        client.addHeader("Authorization", "Bearer " + session.getAPIToken());
        client.put(context, url, null, entity, "application/json", responseHandler);
    }

}
