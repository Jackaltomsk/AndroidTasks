package projects.my.stopwatch.volley.imgur.models;

/**
 * Модель ответа Imgur.
 */
public class ImgurResponse {
    private GalleryImage[] data;
    private boolean success;
    private int status;

    public GalleryImage[] getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }
}
