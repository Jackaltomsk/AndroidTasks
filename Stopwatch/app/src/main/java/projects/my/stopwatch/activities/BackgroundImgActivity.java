package projects.my.stopwatch.activities;

import android.content.Context;
import android.content.res.Configuration;
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
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;

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
    @InstanceState
    int currentPage;

    @ViewById(R.id.recycler_view_img)
    public RecyclerView recyclerView;

    @Bean
    BackgroundImgAdapter adapter;
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

        ConnectivityManager cm = (ConnectivityManager) queueHolder.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (!activeNetwork.isConnectedOrConnecting()) {
            Toast.makeText(queueHolder.getContext(), "Нет подключения к Internet",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            adapter.init(queueHolder.getImageLoader(), this);
            recyclerView.addOnScrollListener(adapter.getViewScrollListener());
            recyclerView.setAdapter(adapter);
        }
    }
}
