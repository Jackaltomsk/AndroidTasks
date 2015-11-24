package projects.my.stopwatch.volley.imgur.models;

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
}