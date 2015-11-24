package projects.my.stopwatch.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import projects.my.stopwatch.R;

public class BackgroundImgAdapter extends RecyclerView.Adapter<BackgroundImgAdapter.ViewHolder> {
    private ImageLoader imageLoader;
    private String[] dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public NetworkImageView imageView;
        public ViewHolder(NetworkImageView v) {
            super(v);
            imageView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BackgroundImgAdapter(String[] myDataset, ImageLoader imgLoader) {
        dataset = myDataset;
        imageLoader = imgLoader;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BackgroundImgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        NetworkImageView v = (NetworkImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_background_img, parent, false);
        // set the view's size, margins, paddings and layout parameters
        // ...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.imageView.setImageUrl(dataset[position], imageLoader);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.length;
    }
}
