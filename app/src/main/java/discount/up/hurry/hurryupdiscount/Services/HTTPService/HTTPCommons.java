package discount.up.hurry.hurryupdiscount.Services.HTTPService;

import java.util.Map;

import discount.up.hurry.hurryupdiscount.Tools.StrSubstitutor;

public final class HTTPCommons {
    public static final String HOST_URL = "http://192.168.1.2:3000";        // development machine ip
//    public static final String HOST_URL = "http://10.0.2.2:3000";         // AVD ip to route to host machine
//    public static final String HOST_URL = "http://api.hurry-up.co:3000";  // 'production' server
//    POST     /v1/users/login
    public static final String LOGIN_URL = HOST_URL + "/v1/users/login";
//    POST     /v1/users
    public static final String REGISTER_URL = HOST_URL + "/v1/users";
//    POST     /v1/users/me
    public static final String ME_URL = HOST_URL + "/v1/users/me";
//    PUT      /v1/users/:id/mobile
    public static final String USER_SET_MOBILE_URL = HOST_URL + "/v1/users/${id}/mobile";
//    POST     /v1/users/:id/like/:client_id/discount/:discount_id
    public static final String USER_LIKE_DISCOUNT_URL = HOST_URL + "/v1/users/${id}/like/${client_id}/discount/${discount_id}";
//    PUT      /v1/users/:id
    public static final String USER_UPDATE_URL = HOST_URL + "/v1/users/${id}";
//    GET      /v1/discounts
    public static final String GET_AVAILABLE_DISCOUNTS_URL = HOST_URL + "/v1/discounts";

    public static String replaceUrlPlaceholders(final String URL, Map<String, String> plaholderValues) {
        StrSubstitutor strSubstitutor = new StrSubstitutor(plaholderValues);
        return strSubstitutor.replace(URL);
    }
//    POST     /v1/users/password
//    GET      /v1/users/password/new
//    GET      /v1/users/password/edit
//    PATCH    /v1/users/password
//    PUT      /v1/users/password
//    GET      /v1/clients
}
