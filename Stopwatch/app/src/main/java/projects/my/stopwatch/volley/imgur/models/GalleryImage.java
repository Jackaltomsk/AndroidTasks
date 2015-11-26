package projects.my.stopwatch.volley.imgur.models;

import projects.my.stopwatch.volley.Constants;

/**
 * Модель результата главной галерии Imgur (возможны галерея и картинка).
 */
public class GalleryImage {
    private String id;
    private boolean is_album;
    private boolean animated;
    private String link;

    public String getId() {
        return id;
    }

    public boolean is_album() {
        return is_album;
    }

    public boolean isAnimated() {
        return animated;
    }

    public String getLink() {
        return link;
    }

    /**
     * Реализует получение ссылки на предпросмотровую версию картинки.
     * @return Возвращает ссылку на тамб для отображения во вью.
     */
    public String getThumbLink() {
        String[] splitted = link.split(id); // здесь всегда 2 чсти.
        String thumbLink = splitted[0] + id + Constants.MEDIUM_THUMB_POSTFIX + splitted[1];
        return thumbLink;
    }
}