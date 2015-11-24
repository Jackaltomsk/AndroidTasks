package projects.my.stopwatch.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import projects.my.stopwatch.R;
import projects.my.stopwatch.adapters.BackgroundImgAdapter;
import projects.my.stopwatch.common.ActivityUtils;
import projects.my.stopwatch.volley.AuthJsonRequest;
import projects.my.stopwatch.volley.Constants;
import projects.my.stopwatch.volley.QueueHolder;
import projects.my.stopwatch.volley.imgur.models.GalleryImage;
import projects.my.stopwatch.volley.imgur.models.ImgurResponse;

@EActivity(R.layout.activity_background_img)
public class BackgroundImgActivity extends AppCompatActivity {
    private static final String TAG = BackgroundImgActivity.class.getSimpleName();

    private static final int PORT_SPAN = 2;
    private static final int LAND_SPAN = 4;

    @ViewById(R.id.recycler_view_img)
    public RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Bean
    QueueHolder queueHolder;

    @AfterViews
    public void init() {
        ActivityUtils.setToolbar(this, true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setVerticalScrollBarEnabled(true);

        boolean isPortrait = getResources().getConfiguration().
                orientation == Configuration.ORIENTATION_PORTRAIT;
        layoutManager = new GridLayoutManager(this, isPortrait ? PORT_SPAN : LAND_SPAN);
        recyclerView.setLayoutManager(layoutManager);

        fetchImages();
    }

    private void fetchImages() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(this, "Нет подключения к Internet", Toast.LENGTH_SHORT).show();
        }
        else {
            queueHolder.addToRequestQueue(new AuthJsonRequest(Constants.UMGUR_BASE +
                    Constants.UMGUR_GALLERY + 0 + Constants.JSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Gson gson = new Gson();
                            ImgurResponse resp = gson.fromJson(response.toString(),
                                    ImgurResponse.class);
                            updateAdapter(resp);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String data = new String(error.networkResponse.data);
                    Log.e(TAG, "Ошибка получения данных.");
                }
            }, Constants.CLIENT_ID + " " + Constants.IMGUR_APP_ID));
        }
    }

    private void updateAdapter(ImgurResponse resp) {
        ArrayList<String> urls = new ArrayList<>(resp.getData().length);
        for (GalleryImage img : resp.getData()) {
            if (!img.is_album() && !img.isAnimated()) urls.add(img.getLink());
        }

        String[] arrUrls = new String[urls.size()];
        // specify an adapter (see also next example)
        adapter = new BackgroundImgAdapter(urls.toArray(arrUrls), queueHolder.getImageLoader());
        recyclerView.setAdapter(adapter);
    }
}
