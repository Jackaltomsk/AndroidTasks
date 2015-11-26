package projects.my.stopwatch.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import projects.my.stopwatch.R;
import projects.my.stopwatch.fragments.FragmentImg;
import projects.my.stopwatch.fragments.FragmentImg_;
import projects.my.stopwatch.volley.QueueHolder;
import projects.my.stopwatch.volley.imgur.models.GalleryImage;

@EBean
public class BackgroundImgAdapter extends RecyclerView.Adapter<BackgroundImgAdapter.ViewHolder> {
    private static final String TAG = BackgroundImgAdapter.class.getSimpleName();

    private ImageLoader imageLoader;
    private GalleryImage[] dataset;
    private Activity activity;

    @Bean
    QueueHolder queueHolder;
    FragmentImg fg;

    /**
     * Данные элемента списка.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private NetworkImageView imageView;

        public ViewHolder(NetworkImageView v) {
            super(v);
            imageView = v;
        }

        public NetworkImageView getImageView() {
            return imageView;
        }
    }

    public BackgroundImgAdapter() {
        fg = new FragmentImg_();
    }

    public void init(GalleryImage[] myDataset, ImageLoader imgLoader, Activity act) {
        dataset = myDataset;
        imageLoader = imgLoader;
        activity = act;
    }

    @Override
    public BackgroundImgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NetworkImageView v = (NetworkImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_background_img, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GalleryImage model = dataset[position];
        holder.imageView.setImageUrl(model.getThumbLink(), imageLoader);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueHolder.getImageLoader().get(model.getLink(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response,
                                           boolean isImmediate) {
                        if (!fg.isAdded()) fg.show(activity.getFragmentManager(), "fg");
                        if (fg.isReady()) fg.setImage(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Ошибка получения данных: " +
                                new String(error.networkResponse.data));
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}
