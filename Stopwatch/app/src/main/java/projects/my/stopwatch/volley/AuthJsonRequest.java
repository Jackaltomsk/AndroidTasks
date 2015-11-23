package projects.my.stopwatch.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Реализует json-запрос с простой авторизацией.
 */
public class AuthJsonRequest extends JsonObjectRequest {
    private final String credentials;

    public AuthJsonRequest(int method, String url, String requestBody,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, String creds) {
        super(method, url, requestBody, listener, errorListener);
        credentials = creds;
    }

    public AuthJsonRequest(String url, Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, String creds) {
        super(url, listener, errorListener);
        credentials = creds;
    }

    public AuthJsonRequest(int method, String url, Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, String creds) {
        super(method, url, listener, errorListener);
        credentials = creds;
    }

    public AuthJsonRequest(int method, String url, JSONObject jsonRequest,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, String creds) {
        super(method, url, jsonRequest, listener, errorListener);
        credentials = creds;
    }

    public AuthJsonRequest(String url, JSONObject jsonRequest,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, String creds) {
        super(url, jsonRequest, listener, errorListener);
        credentials = creds;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HeaderCreator headers = new HeaderCreator();
        headers.AddSimpleAuth(credentials);
        return headers.getHeaders();
    }
}
