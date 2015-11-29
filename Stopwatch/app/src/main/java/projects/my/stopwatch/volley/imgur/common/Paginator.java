package projects.my.stopwatch.volley.imgur.common;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

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
    public static final int ITEMS_PER_PAGE = 24;
    private static final String TAG = Paginator.class.getSimpleName();
    private int realPage; // на реальной странице переменное количество изображений
    private int innerUiPage; // для UI количество изображений на странице фиксировано
    private int uiPage;
    private ArrayList<GalleryImage> images;
    private ImgDatasetUpdated imgDatasetUpdatedListener;

    /**
     * Определяет контракт события добавления данных в кэш.
     */
    public interface ImgDatasetUpdated {
        /**
         * Определяет реакцию на смену данных в пейджере.
         * @param isFirstPage Признак первой страницы, т.е. данные устанавливаются в первый раз.
         */
        void updateData(boolean isFirstPage);
    }

    @Bean
    QueueHolder queueHolder;

    public Paginator() {
        images = new ArrayList<>();
    }

    /**
     * Реализует запрос на получение изображений.
     */
    public void fetchImages() {
        queueHolder.addToRequestQueue(new AuthJsonRequest(Constants.UMGUR_BASE +
                Constants.UMGUR_GALLERY + realPage + Constants.JSON,
                new Response.Listener<JSONObject>() {
                    private boolean isFirsCall = true;

                    @Override
                    public void onResponse(JSONObject response) {
                        ImgurResponse resp = new Gson().fromJson(response.toString(),
                                ImgurResponse.class);
                        // Вычищаем локальный кэш, если там что-то есть.
                        int imageCount = images.size();
                        for (GalleryImage img : resp.getData()) {
                            if (!images.contains(img) && !img.is_album() && !img.isAnimated())
                                images.add(img);
                        }
                        if (isFirsCall && imageCount < images.size()) { // если добавились новые
                            if (imgDatasetUpdatedListener != null) {
                                imgDatasetUpdatedListener.updateData(realPage == 0);
                            }
                            realPage++;
                            isFirsCall = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Ошибка получения данных: " + new String(error.networkResponse.data));
            }
        }, Constants.CLIENT_ID + " " + Constants.IMGUR_APP_ID));
    }

    public void setImgDatasetUpdatedListener(ImgDatasetUpdated imgDatasetUpdatedListener) {
        this.imgDatasetUpdatedListener = imgDatasetUpdatedListener;
    }

    /**
     * Реализует получение следующей страницы.
     * @return Возвращает массив моделей изображений.
     */
    public GalleryImage[] getNextPage() {
        return getGalleryImages(innerUiPage++);
    }

    /**
     * Реализует получение предыдущей страницы.
     * @return Возвращает массив моделей изображений. Если текущая страница - первая,
     * то вернет null.
     */
    public GalleryImage[] getPrevPage() {
        if (innerUiPage == 0) return null;
        return getGalleryImages(innerUiPage == 1 ? 0 : --innerUiPage);
    }

    public int getUiPage() {
        return uiPage;
    }

    /**
     * Реализует получение данных для переданной страницы.
     * @param page Номер ui-страницы.
     * @return Возвращает массив моделей изображений.
     */
    private GalleryImage[] getGalleryImages(int page) {
        int lastValue = page * ITEMS_PER_PAGE + ITEMS_PER_PAGE;
        // запросим новую реальную страницу
        if (images.size() < lastValue) {
            fetchImages();
            innerUiPage = uiPage;
            return null;
        }
        else {
            uiPage = page;
            GalleryImage[] data = new GalleryImage[ITEMS_PER_PAGE];
            for (int i = page * ITEMS_PER_PAGE, j = 0; i < lastValue; i++, j++) {
                data[j] = images.get(i);
            }
            return data;
        }
    }
}
