package projects.my.stopwatch.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import projects.my.stopwatch.R;
import projects.my.stopwatch.fragments.FragmentImg;
import projects.my.stopwatch.fragments.FragmentImg_;
import projects.my.stopwatch.volley.QueueHolder;
import projects.my.stopwatch.volley.imgur.common.Paginator;
import projects.my.stopwatch.volley.imgur.models.GalleryImage;

@EBean
public class BackgroundImgAdapter extends RecyclerView.Adapter<BackgroundImgAdapter.ViewHolder>
        implements Paginator.ImgDatasetUpdated {
    private static final String TAG = BackgroundImgAdapter.class.getSimpleName();

    private ImageLoader imageLoader;
    private GalleryImage[] dataset;
    private Activity activity;
    private FragmentImg fragmentImg;
    private RecyclerView.OnScrollListener viewScrollListener;

    @Bean
    QueueHolder queueHolder;
    @Bean
    Paginator paginator;

    @Override
    public void updateData(boolean isFirstPage) {
        updateDataset(isFirstPage, true);
    }

    private void updateDataset(boolean isFirstPage, boolean getNext) {
        GalleryImage[] data = getNext ? paginator.getNextPage() : paginator.getPrevPage();
        if (data != null) dataset = data;

        if (isFirstPage) this.notifyDataSetChanged();
        else this.notifyItemRangeChanged(0, Paginator.ITEMS_PER_PAGE);
    }

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
        fragmentImg = new FragmentImg_();
        dataset = new GalleryImage[]{};
    }

    public void init(ImageLoader imgLoader, Activity act) {
        imageLoader = imgLoader;
        activity = act;
        paginator.setImgDatasetUpdatedListener(this);
        paginator.fetchImages();
        viewScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager lmgr = (GridLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0) //check for scroll down
                {
                    int lastItemPos = lmgr.findLastCompletelyVisibleItemPosition();
                    int itemCount = lmgr.getItemCount();

                    if (lastItemPos == (itemCount - 1)) {
                        Log.v(TAG, "Достигнут конец страницы.");
                        updateDataset(false, true);
                        recyclerView.scrollToPosition(0);
                        Toast.makeText(activity, "Страница " + paginator.getUiPage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else if (dy < 0) {
                    if (lmgr.findFirstCompletelyVisibleItemPosition() == 0) {
                        Log.v(TAG, "Достигнуто начало страницы.");
                        updateDataset(false, false);
                        if (paginator.getUiPage() > 0) {
                            recyclerView.scrollToPosition(Paginator.ITEMS_PER_PAGE - 1);
                        }
                        Toast.makeText(activity, "Страница " + paginator.getUiPage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
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
                        if (!fragmentImg.isAdded()) {
                            fragmentImg.show(activity.getFragmentManager(),
                                    "fg");
                        }
                        fragmentImg.setImage(response.getBitmap());
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

    public RecyclerView.OnScrollListener getViewScrollListener() {
        return viewScrollListener;
    }
}
