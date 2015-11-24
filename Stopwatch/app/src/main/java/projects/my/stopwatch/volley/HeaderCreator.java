package projects.my.stopwatch.volley;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Конструктор хедеров для запроса.
 */
public class HeaderCreator {
    private static final String TAG = HeaderCreator.class.getSimpleName();
    final Map<String, String> headers = new HashMap<>();

    /**
     *
     * @param key Название заголовка (двоеточие после названия подставляется автоматически).
     * @param value Значение заголовка.
     */
    public void Add(String key, String value) {
        try {
            headers.put(key, value);
        }
        catch (NullPointerException ex) {
            Log.e(TAG, "Ошибка добавления хедера: " + ex);
        }
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    /**
     * Реализует добавление простого заголовка авторизации.
     * @param value Значение (пользователь или другое).
     */
    public void AddSimpleAuth(String value) {
        this.Add("Authorization", value);
    }
}
