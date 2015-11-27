package projects.my.stopwatch.volley.imgur.common;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.util.ArrayList;

import projects.my.stopwatch.volley.AuthJsonRequest;
import projects.my.stopwatch.volley.Constants;
import projects.my.stopwatch.volley.QueueHolder;
import projects.my.stopwatch.volley.imgur.models.GalleryImage;
import projects.my.stopwatch.volley.imgur.models.ImgurResponse;

/**
 * Реализация пагинации в галерее Imgur.
 */
@EBean
public class Paginator {
    private static final int ITEMS_PER_PAGE = 20;
    private static final String TAG = Paginator.class.getSimpleName();
    private int realPage; // на реальной странице переменное количество изображений
    private int uiPage; // для UI количество изображений на странице фиксировано
    private ArrayList<GalleryImage> images;

    @Bean
    QueueHolder queueHolder;

    public Paginator() {
        images = new ArrayList<>();
    }

    @Background
    void fetchImages() {
        queueHolder.addToRequestQueue(new AuthJsonRequest(Constants.UMGUR_BASE +
                Constants.UMGUR_GALLERY + (realPage++) + Constants.JSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ImgurResponse resp = new Gson().fromJson(response.toString(),
                                ImgurResponse.class);
                        // Вычищаем локальный кэш, если там что-то есть.
                        if (images.size() > 0) images.clear();
                        for (GalleryImage img : resp.getData()) {
                            if (!img.is_album() && !img.isAnimated()) images.add(img);
                        }
                        //updateAdapter(resp);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Ошибка получения данных: " + new String(error.networkResponse.data));
            }
        }, Constants.CLIENT_ID + " " + Constants.IMGUR_APP_ID));
    }

    public GalleryImage[] getNextPage() {
        throw new UnsupportedOperationException();
    }
}
