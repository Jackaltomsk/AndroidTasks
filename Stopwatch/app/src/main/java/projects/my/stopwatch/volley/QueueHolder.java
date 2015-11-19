package projects.my.stopwatch.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import org.androidannotations.annotations.EBean;

/**
 * Синглтон очереди запросов Volley.
 */
@EBean(scope = EBean.Scope.Singleton)
public class QueueHolder {
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    static Context ctx;

    QueueHolder(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public RequestQueue getRequestQueue() {        
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
