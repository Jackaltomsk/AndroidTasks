package projects.my.stopwatch.volley;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import java.util.Map;

/**
 * Запрос загрузки изображения с простой авторизацией.
 */
public class AuthImageRequest extends ImageRequest {
    private final String credentials;

    public AuthImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth,
                            int maxHeight, ImageView.ScaleType scaleType,
                            Bitmap.Config decodeConfig, Response.ErrorListener errorListener,
                            String creds) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
        credentials = creds;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HeaderCreator headers = new HeaderCreator();
        headers.AddSimpleAuth(credentials);
        return headers.getHeaders();
    }
}
